package com.yatrisetu.app.presentation.crowd

import com.yatrisetu.app.data.repository.CrowdRepository
import com.yatrisetu.app.domain.model.CrowdFactor
import com.yatrisetu.app.domain.model.CrowdInfo
import com.yatrisetu.app.domain.model.CrowdLevel
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CrowdViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val sampleCrowdInfo = CrowdInfo(
        destinationId = "darjeeling",
        destinationName = "Darjeeling",
        crowdScore = 88,
        crowdLevel = CrowdLevel.VERY_HIGH,
        colorCode = "#DC2626",
        summary = "Tiger Hill sunrise traffic & Mall road choke points at peak.",
        whyCrowded = listOf("Tiger Hill queue exceeding 2 hours"),
        bottlenecks = listOf("Hill Cart Road Ghoom bend"),
        factors = listOf(
            CrowdFactor(
                name = "Traffic Volume",
                key = "traffic",
                rawValue = 90f,
                weightPercentage = 30,
                weightedContribution = 27f,
                description = "Severe arterial delay"
            )
        ),
        liveTrafficStatus = "Congested",
        hotelOccupancyRate = "94%",
        lastUpdated = "2026-09-22T00:00:00Z",
        isLive = true
    )

    private val mockCrowdRepo = object : CrowdRepository {
        var shouldReturnError = false

        override fun getCrowdInfo(
            destinationId: String,
            forceRefresh: Boolean
        ): Flow<Resource<CrowdInfo>> {
            return if (shouldReturnError) {
                flowOf(Resource.Error("Telemetry unreachable"))
            } else {
                flowOf(Resource.Success(data = sampleCrowdInfo, isLive = true))
            }
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
    fun testLoadCrowdEmitsSuccessWithLiveTelemetry() = runTest {
        val viewModel = CrowdViewModel(mockCrowdRepo, "darjeeling")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.crowdInfo)
        assertEquals(88, state.crowdInfo?.crowdScore)
        assertEquals(CrowdLevel.VERY_HIGH, state.crowdInfo?.crowdLevel)
        assertTrue(state.isLive)
        assertTrue(state.freshnessText.contains("LIVE"))
        assertNull(state.errorMessage)
    }

    @Test
    fun testLoadCrowdErrorState() = runTest {
        mockCrowdRepo.shouldReturnError = true
        val viewModel = CrowdViewModel(mockCrowdRepo, "darjeeling")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.crowdInfo)
        assertEquals("Telemetry unreachable", state.errorMessage)
    }
}
