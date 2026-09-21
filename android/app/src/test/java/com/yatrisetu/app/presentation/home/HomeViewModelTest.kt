package com.yatrisetu.app.presentation.home

import com.yatrisetu.app.data.repository.DestinationRepository
import com.yatrisetu.app.domain.model.CrowdLevel
import com.yatrisetu.app.domain.model.Destination
import com.yatrisetu.app.domain.model.DestinationSummary
import com.yatrisetu.app.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val sampleDestinations = listOf(
        DestinationSummary(
            id = "darjeeling",
            name = "Darjeeling",
            tagline = "Queen of Hills",
            region = "Eastern Himalayas",
            state = "West Bengal",
            heroImage = "",
            crowdScore = 88,
            crowdLevel = CrowdLevel.VERY_HIGH,
            avgCostPerDayInr = 4800,
            tags = listOf("Toy Train", "Tea"),
            isLive = true
        ),
        DestinationSummary(
            id = "kalimpong",
            name = "Kalimpong",
            tagline = "Ridge flower haven",
            region = "Eastern Himalayas",
            state = "West Bengal",
            heroImage = "",
            crowdScore = 42,
            crowdLevel = CrowdLevel.MEDIUM,
            avgCostPerDayInr = 2800,
            tags = listOf("Flowers", "Homestay"),
            isLive = true
        ),
        DestinationSummary(
            id = "rishop",
            name = "Rishop",
            tagline = "Scenic quiet village",
            region = "Eastern Himalayas",
            state = "West Bengal",
            heroImage = "",
            crowdScore = 15,
            crowdLevel = CrowdLevel.LOW,
            avgCostPerDayInr = 2200,
            tags = listOf("Kanchenjunga", "Ecotourism"),
            isLive = true
        )
    )

    private val mockRepository = object : DestinationRepository {
        override fun getDestinations(
            query: String?,
            forceRefresh: Boolean
        ): Flow<Resource<List<DestinationSummary>>> {
            return flowOf(Resource.Success(data = sampleDestinations, isLive = true))
        }

        override fun getDestinationDetails(
            id: String,
            forceRefresh: Boolean
        ): Flow<Resource<Destination>> {
            throw NotImplementedError()
        }
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialLoadPopulatesDestinations() = runTest {
        val viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(3, state.destinations.size)
        assertEquals(3, state.filteredDestinations.size)
        assertFalse(state.isOffline)
        assertNull(state.errorMessage)
    }

    @Test
    fun testSearchQueryFiltersList() = runTest {
        val viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("kalim")

        val state = viewModel.uiState.value
        assertEquals("kalim", state.searchQuery)
        assertEquals(1, state.filteredDestinations.size)
        assertEquals("kalimpong", state.filteredDestinations[0].id)
    }

    @Test
    fun testCrowdFilterSelectionFiltersList() = runTest {
        val viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()

        viewModel.onFilterSelected(CrowdLevel.LOW)

        val state = viewModel.uiState.value
        assertEquals(CrowdLevel.LOW, state.selectedCrowdFilter)
        assertEquals(1, state.filteredDestinations.size)
        assertEquals("rishop", state.filteredDestinations[0].id)

        // Toggling same filter clears it
        viewModel.onFilterSelected(CrowdLevel.LOW)
        val resetState = viewModel.uiState.value
        assertNull(resetState.selectedCrowdFilter)
        assertEquals(3, resetState.filteredDestinations.size)
    }
}
