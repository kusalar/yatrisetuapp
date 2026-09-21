package com.yatrisetu.app.presentation.destination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yatrisetu.app.data.repository.CrowdRepository
import com.yatrisetu.app.data.repository.DestinationRepository
import com.yatrisetu.app.domain.model.CrowdInfo
import com.yatrisetu.app.domain.model.Destination
import com.yatrisetu.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DestinationDetailUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val destination: Destination? = null,
    val crowdInfo: CrowdInfo? = null,
    val errorMessage: String? = null
)

class DestinationDetailViewModel(
    private val destinationRepository: DestinationRepository,
    private val crowdRepository: CrowdRepository,
    private val destinationId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(DestinationDetailUiState(isLoading = true))
    val uiState: StateFlow<DestinationDetailUiState> = _uiState.asStateFlow()

    init {
        loadData(forceRefresh = false)
    }

    fun loadData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.destination == null, isRefreshing = forceRefresh) }

            launch {
                destinationRepository.getDestinationDetails(destinationId, forceRefresh).collect { res ->
                    when (res) {
                        is Resource.Loading -> { /* Keep current */ }
                        is Resource.Success -> {
                            _uiState.update { it.copy(destination = res.data, errorMessage = null) }
                        }
                        is Resource.Error -> {
                            if (_uiState.value.destination == null) {
                                _uiState.update { it.copy(errorMessage = res.message) }
                            }
                        }
                    }
                }
            }

            launch {
                crowdRepository.getCrowdInfo(destinationId, forceRefresh).collect { res ->
                    when (res) {
                        is Resource.Loading -> {
                            res.cachedData?.let { cached ->
                                _uiState.update { it.copy(crowdInfo = cached) }
                            }
                        }
                        is Resource.Success -> {
                            _uiState.update { it.copy(crowdInfo = res.data) }
                        }
                        is Resource.Error -> { /* Crowd info is secondary; do not break detail screen */ }
                    }
                }
            }

            _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
        }
    }

    fun refresh() {
        loadData(forceRefresh = true)
    }

    class Factory(
        private val destinationRepository: DestinationRepository,
        private val crowdRepository: CrowdRepository,
        private val destinationId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DestinationDetailViewModel(destinationRepository, crowdRepository, destinationId) as T
        }
    }
}
