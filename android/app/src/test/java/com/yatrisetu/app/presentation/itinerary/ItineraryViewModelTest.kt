package com.yatrisetu.app.presentation.itinerary

import com.yatrisetu.app.data.repository.ItineraryRepository
import com.yatrisetu.app.domain.model.ActivitySlot
import com.yatrisetu.app.domain.model.Itinerary
import com.yatrisetu.app.domain.model.ItineraryDay
import com.yatrisetu.app.domain.model.ItineraryRequest
import com.yatrisetu.app.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
class ItineraryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val sampleGeneratedItinerary = Itinerary(
        itineraryId = "itin_101",
        destinationId = "darjeeling",
        destinationName = "Darjeeling",
        durationDays = 2,
        travelerType = "Solo",
        pace = "Moderate",
        interests = listOf("Nature", "Culture"),
        totalEstimatedBudgetInr = 2500,
        crowdAvoidanceRating = "High",
        localEconomicImpactTag = "COMMUNITY_MANAGED",
        days = listOf(
            ItineraryDay(
                dayNumber = 1,
                theme = "Heritage Ridge",
                overview = "Exploring colonial ridge",
                estimatedBudgetInr = 1200,
                activities = listOf(
                    ActivitySlot(
                        timeSlot = "09:00",
                        period = "Morning",
                        title = "Chowrasta Mall Walk",
                        description = "Scenic morning walk",
                        locationName = "Mall Road",
                        crowdForecast = "Low crowd expected",
                        costEstimateInr = 0,
                        durationHrs = 2f,
                        travelTip = "Visit early",
                        category = "Heritage"
                    )
                ),
                transitAdvice = "Walking"
            )
        ),
        aiGeneratedNote = "Balanced plan",
        sustainabilityScore = 88,
        sustainabilityClassification = "EXCELLENT",
        weatherAdaptationNotice = null,
        whyThisItinerary = listOf("High nature immersion"),
        aiProviderUsed = "rule_based_fallback",
        optimizationHistory = emptyList(),
        isLive = true
    )

    private val sampleOptimizedItinerary = sampleGeneratedItinerary.copy(
        itineraryId = "itin_101_opt",
        sustainabilityScore = 96,
        crowdAvoidanceRating = "Excellent",
        optimizationHistory = listOf("AVOID_CROWDS")
    )

    private val fakeRepo = object : ItineraryRepository {
        var shouldFailGenerate = false
        var generateErrorMessage = "Connection error"
        var shouldFailOptimize = false
        var optimizeErrorMessage = "Optimization failed"
        var cachedItinerary: Itinerary? = null

        override fun generateItinerary(request: ItineraryRequest): Flow<Resource<Itinerary>> = flow {
            emit(Resource.Loading())
            if (shouldFailGenerate) {
                emit(Resource.Error(generateErrorMessage))
            } else {
                emit(Resource.Success(sampleGeneratedItinerary, isLive = true))
            }
        }

        override fun optimizeItinerary(
            directive: String,
            customInstruction: String?,
            currentItinerary: Itinerary
        ): Flow<Resource<Itinerary>> = flow {
            emit(Resource.Loading())
            if (shouldFailOptimize) {
                emit(Resource.Error(optimizeErrorMessage, cachedData = currentItinerary))
            } else {
                emit(Resource.Success(sampleOptimizedItinerary, isLive = true))
            }
        }

        override fun getCachedItinerary(destinationId: String): Itinerary? = cachedItinerary

        override fun clearCache() {
            cachedItinerary = null
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo.shouldFailGenerate = false
        fakeRepo.shouldFailOptimize = false
        fakeRepo.cachedItinerary = null
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialFormState() = runTest(testDispatcher) {
        val viewModel = ItineraryViewModel(
            itineraryRepository = fakeRepo,
            initialDestinationId = "darjeeling",
            initialDestinationName = "Darjeeling"
        )

        val state = viewModel.uiState.value
        assertEquals(ItineraryUiMode.FORM, state.mode)
        assertEquals("darjeeling", state.destinationId)
        assertEquals("Darjeeling", state.destinationName)
        assertEquals(3, state.durationDays)
        assertEquals("SOLO", state.travelerType)
        assertEquals("BALANCED", state.pace)
        assertTrue(state.interests.contains("Nature"))
        assertTrue(state.interests.contains("Culture"))
        assertEquals("MODERATE", state.budgetLevel)
        assertTrue(state.canGenerate)
        assertFalse(state.hasItinerary)
    }

    @Test
    fun testFormStateMutations() = runTest(testDispatcher) {
        val viewModel = ItineraryViewModel(
            itineraryRepository = fakeRepo,
            initialDestinationId = "darjeeling"
        )

        viewModel.setDuration(4)
        assertEquals(4, viewModel.uiState.value.durationDays)

        viewModel.setDuration(10) // exceeds 5 -> coerced
        assertEquals(5, viewModel.uiState.value.durationDays)

        viewModel.setTravelerType("FAMILY")
        assertEquals("FAMILY", viewModel.uiState.value.travelerType)

        viewModel.setPace("RELAXED")
        assertEquals("RELAXED", viewModel.uiState.value.pace)

        viewModel.setBudgetLevel("LUXURY")
        assertEquals("LUXURY", viewModel.uiState.value.budgetLevel)

        viewModel.toggleInterest("Adventure")
        assertTrue(viewModel.uiState.value.interests.contains("Adventure"))

        viewModel.setOptimizeForWeather(false)
        assertFalse(viewModel.uiState.value.optimizeForWeather)
    }

    @Test
    fun testSuccessfulItineraryGenerationFlow() = runTest(testDispatcher) {
        val viewModel = ItineraryViewModel(
            itineraryRepository = fakeRepo,
            initialDestinationId = "darjeeling"
        )

        viewModel.generateItinerary()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ItineraryUiMode.RESULT, state.mode)
        assertNotNull(state.currentItinerary)
        assertEquals("itin_101", state.currentItinerary?.itineraryId)
        assertEquals(88, state.currentItinerary?.sustainabilityScore)
        assertNull(state.errorMessage)
        assertFalse(state.isFromCache)
    }

    @Test
    fun testFailedItineraryGenerationFlow() = runTest(testDispatcher) {
        fakeRepo.shouldFailGenerate = true
        fakeRepo.generateErrorMessage = "AI itinerary generation needs an internet connection."

        val viewModel = ItineraryViewModel(
            itineraryRepository = fakeRepo,
            initialDestinationId = "darjeeling"
        )

        viewModel.generateItinerary()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ItineraryUiMode.ERROR, state.mode)
        assertNull(state.currentItinerary)
        assertEquals("AI itinerary generation needs an internet connection.", state.errorMessage)
    }

    @Test
    fun testSuccessfulOptimizationFlow() = runTest(testDispatcher) {
        val viewModel = ItineraryViewModel(
            itineraryRepository = fakeRepo,
            initialDestinationId = "darjeeling"
        )

        // Generate first
        viewModel.generateItinerary()
        testDispatcher.scheduler.advanceUntilIdle()

        // Optimize
        viewModel.optimizeItinerary("AVOID_CROWDS")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ItineraryUiMode.RESULT, state.mode)
        assertEquals("itin_101_opt", state.currentItinerary?.itineraryId)
        assertEquals(96, state.currentItinerary?.sustainabilityScore)
        assertEquals("itin_101", state.previousItinerary?.itineraryId) // previous itinerary preserved
        assertEquals("AVOID_CROWDS", state.activeOptimizationDirective)
    }

    @Test
    fun testOptimizationFailureRetainsCurrentItinerary() = runTest(testDispatcher) {
        val viewModel = ItineraryViewModel(
            itineraryRepository = fakeRepo,
            initialDestinationId = "darjeeling"
        )

        // Generate first
        viewModel.generateItinerary()
        testDispatcher.scheduler.advanceUntilIdle()

        // Optimize with error
        fakeRepo.shouldFailOptimize = true
        fakeRepo.optimizeErrorMessage = "Optimization took longer than expected. Please retry."
        viewModel.optimizeItinerary("AVOID_CROWDS")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ItineraryUiMode.RESULT, state.mode)
        // Original itinerary is retained
        assertEquals("itin_101", state.currentItinerary?.itineraryId)
        assertEquals("Optimization took longer than expected. Please retry.", state.errorMessage)
    }

    @Test
    fun testRetryFromErrorState() = runTest(testDispatcher) {
        fakeRepo.shouldFailGenerate = true
        val viewModel = ItineraryViewModel(
            itineraryRepository = fakeRepo,
            initialDestinationId = "darjeeling"
        )

        viewModel.generateItinerary()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(ItineraryUiMode.ERROR, viewModel.uiState.value.mode)

        // Now backend recovers and user retries
        fakeRepo.shouldFailGenerate = false
        viewModel.retry()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(ItineraryUiMode.RESULT, viewModel.uiState.value.mode)
        assertEquals("itin_101", viewModel.uiState.value.currentItinerary?.itineraryId)
    }

    @Test
    fun testEditPreferencesResetsToForm() = runTest(testDispatcher) {
        val viewModel = ItineraryViewModel(
            itineraryRepository = fakeRepo,
            initialDestinationId = "darjeeling"
        )

        viewModel.generateItinerary()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(ItineraryUiMode.RESULT, viewModel.uiState.value.mode)

        viewModel.editPreferences()
        assertEquals(ItineraryUiMode.FORM, viewModel.uiState.value.mode)
    }
}
