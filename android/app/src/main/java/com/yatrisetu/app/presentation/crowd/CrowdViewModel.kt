package com.yatrisetu.app.presentation.crowd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yatrisetu.app.data.repository.CrowdRepository
import com.yatrisetu.app.domain.model.CrowdInfo
import com.yatrisetu.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CrowdUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val crowdInfo: CrowdInfo? = null,
    val isLive: Boolean = true,
    val freshnessText: String = "",
    val errorMessage: String? = null
)

class CrowdViewModel(
    private val repository: CrowdRepository,
    private val destinationId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(CrowdUiState(isLoading = true))
    val uiState: StateFlow<CrowdUiState> = _uiState.asStateFlow()

    init {
        loadCrowd(forceRefresh = false)
    }

    fun loadCrowd(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            repository.getCrowdInfo(destinationId, forceRefresh = forceRefresh).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { current ->
                            current.copy(
                                isLoading = current.crowdInfo == null && resource.cachedData == null,
                                isRefreshing = forceRefresh,
                                crowdInfo = resource.cachedData ?: current.crowdInfo,
                                isLive = false,
                                freshnessText = resource.cachedData?.freshnessLabel ?: "Loading telemetry..."
                            )
                        }
                    }
                    is Resource.Success -> {
                        _uiState.update { current ->
                            current.copy(
                                isLoading = false,
                                isRefreshing = false,
                                crowdInfo = resource.data,
                                isLive = resource.isLive,
                                freshnessText = resource.data.freshnessLabel,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { current ->
                            current.copy(
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = resource.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun refresh() {
        loadCrowd(forceRefresh = true)
    }

    class Factory(
        private val repository: CrowdRepository,
        private val destinationId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CrowdViewModel(repository, destinationId) as T
        }
    }
}
