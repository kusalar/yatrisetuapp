package com.yatrisetu.app.presentation.alternatives

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yatrisetu.app.data.repository.AlternativesRepository
import com.yatrisetu.app.data.repository.CrowdRepository
import com.yatrisetu.app.domain.model.AlternativeDestination
import com.yatrisetu.app.domain.model.AlternativesData
import com.yatrisetu.app.domain.model.CrowdInfo
import com.yatrisetu.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FlowAdvisorUiState(
    val originDestinationId: String = "darjeeling",
    val originCrowdInfo: CrowdInfo? = null,
    val isLoadingCrowd: Boolean = false,
    val alternativesData: AlternativesData? = null,
    val isLoadingAlternatives: Boolean = false,
    val errorMessage: String? = null,
    val selectedAlternative: AlternativeDestination? = null,
    val isAccepting: Boolean = false
) {
    val isLoading: Boolean
        get() = isLoadingCrowd || isLoadingAlternatives

    val hasAlternatives: Boolean
        get() = alternativesData != null && alternativesData.alternatives.isNotEmpty()
}

class FlowAdvisorViewModel(
    private val alternativesRepository: AlternativesRepository,
    private val crowdRepository: CrowdRepository,
    private val originDestinationId: String = "darjeeling"
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlowAdvisorUiState(originDestinationId = originDestinationId))
    val uiState: StateFlow<FlowAdvisorUiState> = _uiState.asStateFlow()

    init {
        loadData(originDestinationId)
    }

    fun loadData(destinationId: String = originDestinationId, forceRefresh: Boolean = false) {
        val normId = destinationId.lowercase().trim()
        _uiState.update { it.copy(originDestinationId = normId, errorMessage = null) }
        loadOriginCrowd(normId, forceRefresh)
        loadAlternatives(normId, forceRefresh)
    }

    fun loadOriginCrowd(destinationId: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            crowdRepository.getCrowdInfo(destinationId, forceRefresh).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoadingCrowd = true,
                                originCrowdInfo = result.cachedData ?: it.originCrowdInfo
                            )
                        }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoadingCrowd = false,
                                originCrowdInfo = result.data
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoadingCrowd = false,
                                originCrowdInfo = result.cachedData ?: it.originCrowdInfo
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadAlternatives(destinationId: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            alternativesRepository.getAlternatives(destinationId, forceRefresh).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoadingAlternatives = true,
                                alternativesData = result.cachedData ?: it.alternativesData
                            )
                        }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoadingAlternatives = false,
                                alternativesData = result.data,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoadingAlternatives = false,
                                alternativesData = result.cachedData ?: it.alternativesData,
                                errorMessage = if (it.alternativesData == null && result.cachedData == null) {
                                    result.message
                                } else null
                            )
                        }
                    }
                }
            }
        }
    }

    fun selectAlternativeForComparison(alternative: AlternativeDestination) {
        _uiState.update { it.copy(selectedAlternative = alternative) }
    }

    fun dismissComparison() {
        _uiState.update { it.copy(selectedAlternative = null) }
    }

    fun acceptAlternativeAndExplore(
        alternative: AlternativeDestination,
        onNavigateToDestination: (String) -> Unit
    ) {
        val currentOriginId = _uiState.value.originDestinationId
        viewModelScope.launch {
            _uiState.update { it.copy(isAccepting = true) }
            // Notify backend of alternative acceptance (fire and proceed safely)
            try {
                alternativesRepository.acceptAlternative(
                    originId = currentOriginId,
                    alternativeId = alternative.id,
                    similarityScore = alternative.similarityScore
                )
            } catch (e: Exception) {
                // Do not block traveler flow if acceptance telemetry encounters network glitch
            } finally {
                _uiState.update { it.copy(isAccepting = false) }
                onNavigateToDestination(alternative.id)
            }
        }
    }

    fun retry() {
        loadData(_uiState.value.originDestinationId, forceRefresh = true)
    }

    class Factory(
        private val alternativesRepository: AlternativesRepository,
        private val crowdRepository: CrowdRepository,
        private val originDestinationId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FlowAdvisorViewModel(
                alternativesRepository = alternativesRepository,
                crowdRepository = crowdRepository,
                originDestinationId = originDestinationId
            ) as T
        }
    }
}
