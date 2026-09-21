package com.yatrisetu.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yatrisetu.app.data.repository.DestinationRepository
import com.yatrisetu.app.domain.model.CrowdLevel
import com.yatrisetu.app.domain.model.DestinationSummary
import com.yatrisetu.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val destinations: List<DestinationSummary> = emptyList(),
    val filteredDestinations: List<DestinationSummary> = emptyList(),
    val searchQuery: String = "",
    val selectedCrowdFilter: CrowdLevel? = null,
    val isOffline: Boolean = false,
    val freshnessNotice: String? = null,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val repository: DestinationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDestinations(forceRefresh = false)
    }

    fun loadDestinations(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            repository.getDestinations(forceRefresh = forceRefresh).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { current ->
                            current.copy(
                                isLoading = current.destinations.isEmpty() && resource.cachedData == null,
                                isRefreshing = forceRefresh,
                                destinations = resource.cachedData ?: current.destinations,
                                filteredDestinations = applyFilter(
                                    list = resource.cachedData ?: current.destinations,
                                    query = current.searchQuery,
                                    filter = current.selectedCrowdFilter
                                )
                            )
                        }
                    }
                    is Resource.Success -> {
                        val isOffline = !resource.isLive
                        val notice = if (isOffline) {
                            val mins = resource.cachedAt?.let { (System.currentTimeMillis() - it) / 60000 } ?: 0
                            if (mins <= 1) "Offline mode · Showing cached data from just now"
                            else "Offline mode · Showing cached data from ${mins}m ago"
                        } else null

                        _uiState.update { current ->
                            current.copy(
                                isLoading = false,
                                isRefreshing = false,
                                destinations = resource.data,
                                filteredDestinations = applyFilter(
                                    list = resource.data,
                                    query = current.searchQuery,
                                    filter = current.selectedCrowdFilter
                                ),
                                isOffline = isOffline,
                                freshnessNotice = notice,
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

    fun onSearchQueryChanged(newQuery: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = newQuery,
                filteredDestinations = applyFilter(
                    list = current.destinations,
                    query = newQuery,
                    filter = current.selectedCrowdFilter
                )
            )
        }
    }

    fun onFilterSelected(filter: CrowdLevel?) {
        _uiState.update { current ->
            val nextFilter = if (current.selectedCrowdFilter == filter) null else filter
            current.copy(
                selectedCrowdFilter = nextFilter,
                filteredDestinations = applyFilter(
                    list = current.destinations,
                    query = current.searchQuery,
                    filter = nextFilter
                )
            )
        }
    }

    private fun applyFilter(
        list: List<DestinationSummary>,
        query: String,
        filter: CrowdLevel?
    ): List<DestinationSummary> {
        return list.filter { item ->
            val matchesQuery = if (query.isBlank()) true else {
                val q = query.trim().lowercase()
                item.name.lowercase().contains(q) ||
                    item.region.lowercase().contains(q) ||
                    item.state.lowercase().contains(q) ||
                    item.tags.any { it.lowercase().contains(q) }
            }
            val matchesFilter = if (filter == null) true else item.crowdLevel == filter
            matchesQuery && matchesFilter
        }
    }

    class Factory(private val repository: DestinationRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
