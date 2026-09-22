package com.yatrisetu.app.presentation.itinerary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yatrisetu.app.data.repository.ItineraryRepository
import com.yatrisetu.app.domain.model.Itinerary
import com.yatrisetu.app.domain.model.ItineraryRequest
import com.yatrisetu.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ItineraryUiMode {
    FORM,
    GENERATING,
    RESULT,
    OPTIMIZING,
    ERROR
}

data class ItineraryUiState(
    val mode: ItineraryUiMode = ItineraryUiMode.FORM,
    // Planning Form fields
    val destinationId: String = "darjeeling",
    val destinationName: String = "Darjeeling",
    val durationDays: Int = 3,
    val travelerType: String = "SOLO",
    val pace: String = "BALANCED",
    val interests: Set<String> = setOf("Nature", "Culture"),
    val budgetLevel: String = "MODERATE",
    val startDate: String? = null,
    val optimizeForWeather: Boolean = true,

    // Generated & Optimized Result
    val currentItinerary: Itinerary? = null,
    val previousItinerary: Itinerary? = null, // for before/after comparison
    val activeOptimizationDirective: String? = null,
    val selectedDayIndex: Int = 0,
    val progressMessage: String = "Planning your journey…",
    val errorMessage: String? = null,
    val isFromCache: Boolean = false
) {
    val canGenerate: Boolean
        get() = destinationId.isNotBlank() && durationDays in 1..5

    val hasItinerary: Boolean
        get() = currentItinerary != null && currentItinerary.days.isNotEmpty()
}

class ItineraryViewModel(
    private val itineraryRepository: ItineraryRepository,
    initialDestinationId: String = "darjeeling",
    initialDestinationName: String = "Darjeeling"
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ItineraryUiState(
            destinationId = initialDestinationId.lowercase().trim(),
            destinationName = initialDestinationName
        )
    )
    val uiState: StateFlow<ItineraryUiState> = _uiState.asStateFlow()

    init {
        // If there's already a cached itinerary for this destination, check it
        val cached = itineraryRepository.getCachedItinerary(initialDestinationId)
        if (cached != null) {
            _uiState.update {
                it.copy(
                    currentItinerary = cached,
                    isFromCache = true
                )
            }
        }
    }

    fun setDestination(destinationId: String, destinationName: String) {
        val normId = destinationId.lowercase().trim()
        val displayName = if (destinationName.isNotBlank()) destinationName else normId.replaceFirstChar { it.uppercase() }
        val cached = itineraryRepository.getCachedItinerary(normId)
        _uiState.update {
            it.copy(
                destinationId = normId,
                destinationName = displayName,
                currentItinerary = cached,
                isFromCache = cached != null,
                mode = if (cached != null && it.mode == ItineraryUiMode.RESULT) ItineraryUiMode.RESULT else it.mode
            )
        }
    }

    fun setDuration(days: Int) {
        val validDays = days.coerceIn(1, 5)
        _uiState.update { it.copy(durationDays = validDays) }
    }

    fun setTravelerType(type: String) {
        _uiState.update { it.copy(travelerType = type) }
    }

    fun setPace(pace: String) {
        _uiState.update { it.copy(pace = pace) }
    }

    fun toggleInterest(interest: String) {
        _uiState.update { state ->
            val updated = if (state.interests.contains(interest)) {
                if (state.interests.size > 1) state.interests - interest else state.interests
            } else {
                state.interests + interest
            }
            state.copy(interests = updated)
        }
    }

    fun setBudgetLevel(budget: String) {
        _uiState.update { it.copy(budgetLevel = budget) }
    }

    fun setStartDate(date: String?) {
        _uiState.update { it.copy(startDate = date) }
    }

    fun setOptimizeForWeather(enabled: Boolean) {
        _uiState.update { it.copy(optimizeForWeather = enabled) }
    }

    fun selectDay(dayIndex: Int) {
        _uiState.update { it.copy(selectedDayIndex = dayIndex) }
    }

    fun editPreferences() {
        _uiState.update { it.copy(mode = ItineraryUiMode.FORM, errorMessage = null) }
    }

    fun generateItinerary() {
        val state = _uiState.value
        val request = ItineraryRequest(
            destinationId = state.destinationId,
            durationDays = state.durationDays,
            travelerType = state.travelerType,
            pace = state.pace,
            interests = state.interests.toList(),
            budgetLevel = state.budgetLevel,
            startDate = state.startDate,
            optimizeForWeather = state.optimizeForWeather
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    mode = ItineraryUiMode.GENERATING,
                    progressMessage = "Planning your journey…",
                    errorMessage = null,
                    selectedDayIndex = 0
                )
            }

            itineraryRepository.generateItinerary(request).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                mode = ItineraryUiMode.GENERATING,
                                progressMessage = "Balancing experiences & crowd conditions…"
                            )
                        }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                mode = ItineraryUiMode.RESULT,
                                currentItinerary = result.data,
                                isFromCache = !result.isLive,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                mode = if (result.cachedData != null) ItineraryUiMode.RESULT else ItineraryUiMode.ERROR,
                                currentItinerary = result.cachedData ?: it.currentItinerary,
                                isFromCache = result.cachedData != null,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun optimizeItinerary(directive: String, customInstruction: String? = null) {
        val current = _uiState.value.currentItinerary ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    mode = ItineraryUiMode.OPTIMIZING,
                    activeOptimizationDirective = directive,
                    progressMessage = "Optimizing your itinerary for ${directive.replace('_', ' ').lowercase()}…",
                    errorMessage = null
                )
            }

            itineraryRepository.optimizeItinerary(
                directive = directive,
                customInstruction = customInstruction,
                currentItinerary = current
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                mode = ItineraryUiMode.OPTIMIZING,
                                progressMessage = "Adjusting timeline with real-time conditions…"
                            )
                        }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                mode = ItineraryUiMode.RESULT,
                                previousItinerary = current,
                                currentItinerary = result.data,
                                isFromCache = !result.isLive,
                                activeOptimizationDirective = directive,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                mode = ItineraryUiMode.RESULT,
                                currentItinerary = result.cachedData ?: it.currentItinerary,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun retry() {
        if (_uiState.value.mode == ItineraryUiMode.ERROR) {
            generateItinerary()
        }
    }

    class Factory(
        private val itineraryRepository: ItineraryRepository,
        private val destinationId: String = "darjeeling",
        private val destinationName: String = "Darjeeling"
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ItineraryViewModel(
                itineraryRepository = itineraryRepository,
                initialDestinationId = destinationId,
                initialDestinationName = destinationName
            ) as T
        }
    }
}
