package com.yatrisetu.app.presentation.alternatives

import com.yatrisetu.app.data.repository.AlternativesRepository
import com.yatrisetu.app.data.repository.CrowdRepository
import com.yatrisetu.app.domain.model.AlternativeAcceptance
import com.yatrisetu.app.domain.model.AlternativeDestination
import com.yatrisetu.app.domain.model.AlternativesData
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
class FlowAdvisorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val sampleOriginCrowd = CrowdInfo(
        destinationId = "darjeeling",
        destinationName = "Darjeeling",
        crowdScore = 88,
        crowdLevel = CrowdLevel.VERY_HIGH,
        summary = "Tiger Hill sunrise traffic bottleneck.",
        isLive = true
    )

    private val sampleAlternative = AlternativeDestination(
        id = "kalimpong",
        name = "Kalimpong",
        tagline = "Ridge orchid sanctuaries",
        heroImage = "https://test.img",
        crowdScore = 35,
        crowdLevel = CrowdLevel.LOW,
        similarityScore = 88,
        crowdReductionPercent = 60,
        distanceKm = 50f,
        estimatedCostPerDay = 2500,
        costDifferencePercent = 35,
        reasons = listOf("Lower crowd pressure with serene mountain views.")
    )

    private val sampleAlternativesData = AlternativesData(
        originDestinationId = "darjeeling",
        originDestinationName = "Darjeeling",
        originCrowdScore = 88,
        originCrowdLevel = CrowdLevel.VERY_HIGH,
        alternatives = listOf(sampleAlternative),
        isLive = true
    )

    private val fakeCrowdRepo = object : CrowdRepository {
        var shouldFail = false
        override fun getCrowdInfo(destinationId: String, forceRefresh: Boolean): Flow<Resource<CrowdInfo>> {
            return if (shouldFail) {
                flowOf(Resource.Error("Telemetry offline"))
            } else {
                flowOf(Resource.Success(data = sampleOriginCrowd, isLive = true))
            }
        }
    }

    private val fakeAlternativesRepo = object : AlternativesRepository {
        var shouldFail = false
        var acceptCalledWith: Pair<String, String>? = null

        override fun getAlternatives(originId: String, forceRefresh: Boolean): Flow<Resource<AlternativesData>> {
            return if (shouldFail) {
                flowOf(Resource.Error("Alternatives server unavailable"))
            } else {
                flowOf(Resource.Success(data = sampleAlternativesData, isLive = true))
            }
        }

        override suspend fun acceptAlternative(
            originId: String,
            alternativeId: String,
            similarityScore: Int?,
            sessionId: String?
        ): Result<AlternativeAcceptance> {
            acceptCalledWith = Pair(originId, alternativeId)
            return Result.success(
                AlternativeAcceptance(
                    status = "recorded",
                    eventId = "evt-123",
                    originalDestinationId = originId,
                    alternativeDestinationId = alternativeId
                )
            )
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialDataLoadSuccess() = runTest(testDispatcher) {
        val viewModel = FlowAdvisorViewModel(
            alternativesRepository = fakeAlternativesRepo,
            crowdRepository = fakeCrowdRepo,
            originDestinationId = "darjeeling"
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.originCrowdInfo)
        assertEquals("Darjeeling", state.originCrowdInfo?.destinationName)
        assertEquals(88, state.originCrowdInfo?.crowdScore)

        assertNotNull(state.alternativesData)
        assertEquals(1, state.alternativesData?.alternatives?.size)
        assertEquals("kalimpong", state.alternativesData?.alternatives?.first()?.id)
        assertNull(state.errorMessage)
    }

    @Test
    fun testErrorStateAndRetry() = runTest(testDispatcher) {
        fakeAlternativesRepo.shouldFail = true

        val viewModel = FlowAdvisorViewModel(
            alternativesRepository = fakeAlternativesRepo,
            crowdRepository = fakeCrowdRepo,
            originDestinationId = "darjeeling"
        )

        advanceUntilIdle()

        val errorState = viewModel.uiState.value
        assertEquals("Alternatives server unavailable", errorState.errorMessage)
        assertNull(errorState.alternativesData)

        // Fix repository and retry
        fakeAlternativesRepo.shouldFail = false
        viewModel.retry()
        advanceUntilIdle()

        val recoveredState = viewModel.uiState.value
        assertNull(recoveredState.errorMessage)
        assertNotNull(recoveredState.alternativesData)
    }

    @Test
    fun testSelectAlternativeForComparisonAndDismiss() = runTest(testDispatcher) {
        val viewModel = FlowAdvisorViewModel(
            alternativesRepository = fakeAlternativesRepo,
            crowdRepository = fakeCrowdRepo,
            originDestinationId = "darjeeling"
        )

        advanceUntilIdle()

        assertNull(viewModel.uiState.value.selectedAlternative)

        viewModel.selectAlternativeForComparison(sampleAlternative)
        assertEquals("kalimpong", viewModel.uiState.value.selectedAlternative?.id)

        viewModel.dismissComparison()
        assertNull(viewModel.uiState.value.selectedAlternative)
    }

    @Test
    fun testAcceptAlternativeAndExploreNavigation() = runTest(testDispatcher) {
        val viewModel = FlowAdvisorViewModel(
            alternativesRepository = fakeAlternativesRepo,
            crowdRepository = fakeCrowdRepo,
            originDestinationId = "darjeeling"
        )

        advanceUntilIdle()

        var navigatedToId: String? = null
        viewModel.acceptAlternativeAndExplore(sampleAlternative) { id ->
            navigatedToId = id
        }

        advanceUntilIdle()

        assertEquals("kalimpong", navigatedToId)
        assertEquals(Pair("darjeeling", "kalimpong"), fakeAlternativesRepo.acceptCalledWith)
    }

    @Test
    fun testAndroidDoesNotRecomputeBackendAuthoritativeValues() = runTest(testDispatcher) {
        val viewModel = FlowAdvisorViewModel(
            alternativesRepository = fakeAlternativesRepo,
            crowdRepository = fakeCrowdRepo,
            originDestinationId = "darjeeling"
        )

        advanceUntilIdle()

        val alt = viewModel.uiState.value.alternativesData!!.alternatives[0]
        // Explicit check: Android displays backend values exactly without modifying them
        assertEquals(sampleAlternative.crowdScore, alt.crowdScore)
        assertEquals(sampleAlternative.crowdReductionPercent, alt.crowdReductionPercent)
        assertEquals(sampleAlternative.similarityScore, alt.similarityScore)
        assertEquals(sampleAlternative.distanceKm, alt.distanceKm, 0.001f)
    }
}
