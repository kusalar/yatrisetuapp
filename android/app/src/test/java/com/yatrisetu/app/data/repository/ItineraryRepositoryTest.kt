package com.yatrisetu.app.data.repository

import com.yatrisetu.app.data.remote.YatriSetuApi
import com.yatrisetu.app.data.remote.dto.ActivitySlotDto
import com.yatrisetu.app.data.remote.dto.AlternativeAcceptanceRequestDto
import com.yatrisetu.app.data.remote.dto.AlternativeAcceptanceResponseDto
import com.yatrisetu.app.data.remote.dto.AlternativesResponseDto
import com.yatrisetu.app.data.remote.dto.CrowdResponseDto
import com.yatrisetu.app.data.remote.dto.DestinationDetailDto
import com.yatrisetu.app.data.remote.dto.DestinationSummaryDto
import com.yatrisetu.app.data.remote.dto.ItineraryDayDto
import com.yatrisetu.app.data.remote.dto.ItineraryOptimizeRequestDto
import com.yatrisetu.app.data.remote.dto.ItineraryRequestDto
import com.yatrisetu.app.data.remote.dto.ItineraryResponseDto
import com.yatrisetu.app.domain.model.ActivitySlot
import com.yatrisetu.app.domain.model.Itinerary
import com.yatrisetu.app.domain.model.ItineraryDay
import com.yatrisetu.app.domain.model.ItineraryRequest
import com.yatrisetu.app.util.Resource
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

class FakeItineraryApi : YatriSetuApi {
    var shouldFailWithNetwork: Boolean = false
    var shouldFailWithTimeout: Boolean = false
    var httpErrorCode: Int? = null
    var httpErrorMessage: String = ""

    var mockGenerateResponse: ItineraryResponseDto = ItineraryResponseDto(
        itineraryId = "itin_1",
        destinationId = "darjeeling",
        destinationName = "Darjeeling",
        durationDays = 2,
        days = listOf(
            ItineraryDayDto(
                dayNumber = 1,
                theme = "Heritage Walk",
                overview = "Walking old trails",
                estimatedBudgetInr = 1500,
                activities = listOf(
                    ActivitySlotDto(
                        timeSlot = "09:00",
                        period = "Morning",
                        title = "Mall Road",
                        description = "Scenic stroll",
                        locationName = "Chowrasta",
                        crowdForecast = "Low",
                        costEstimateInr = 0,
                        durationHrs = 2f,
                        travelTip = "Morning is best",
                        category = "Heritage"
                    )
                )
            )
        ),
        sustainabilityScore = 88,
        crowdAvoidanceRating = "High",
        totalEstimatedBudgetInr = 3000
    )

    var mockOptimizeResponse: ItineraryResponseDto = ItineraryResponseDto(
        itineraryId = "itin_1_opt",
        destinationId = "darjeeling",
        destinationName = "Darjeeling",
        durationDays = 2,
        days = listOf(
            ItineraryDayDto(
                dayNumber = 1,
                theme = "Quiet Heritage Walk",
                overview = "Observatory peace",
                estimatedBudgetInr = 1500,
                activities = listOf(
                    ActivitySlotDto(
                        timeSlot = "08:00",
                        period = "Morning",
                        title = "Observatory Hill Early Walk",
                        description = "Early morning calm visit",
                        locationName = "Observatory Hill",
                        crowdForecast = "Zero queue",
                        costEstimateInr = 0,
                        durationHrs = 2f,
                        travelTip = "Wear light shoes",
                        category = "Heritage"
                    )
                )
            )
        ),
        sustainabilityScore = 95,
        crowdAvoidanceRating = "Excellent",
        optimizationHistory = listOf("AVOID_CROWDS")
    )

    override suspend fun generateItinerary(request: ItineraryRequestDto): Response<ItineraryResponseDto> {
        if (shouldFailWithNetwork) throw ConnectException("Connection refused")
        if (shouldFailWithTimeout) throw SocketTimeoutException("Read timeout")
        if (httpErrorCode != null) {
            val errorBody = "{\"detail\":\"$httpErrorMessage\"}".toResponseBody("application/json".toMediaTypeOrNull())
            return Response.error(httpErrorCode!!, errorBody)
        }
        return Response.success(mockGenerateResponse)
    }

    override suspend fun optimizeItinerary(request: ItineraryOptimizeRequestDto): Response<ItineraryResponseDto> {
        if (shouldFailWithNetwork) throw ConnectException("Connection refused")
        if (shouldFailWithTimeout) throw SocketTimeoutException("Read timeout")
        if (httpErrorCode != null) {
            val errorBody = "{\"detail\":\"$httpErrorMessage\"}".toResponseBody("application/json".toMediaTypeOrNull())
            return Response.error(httpErrorCode!!, errorBody)
        }
        return Response.success(mockOptimizeResponse)
    }

    override suspend fun getDestinations(query: String?, crowdLevel: String?): Response<List<DestinationSummaryDto>> = Response.success(emptyList())
    override suspend fun getDestinationDetails(id: String): Response<DestinationDetailDto> = Response.success(DestinationDetailDto(id = id, name = "Test $id"))
    override suspend fun getDestinationCrowd(id: String): Response<CrowdResponseDto> = Response.success(CrowdResponseDto(destinationId = id, destinationName = "Test $id"))
    override suspend fun getDestinationAlternatives(id: String): Response<AlternativesResponseDto> = Response.success(AlternativesResponseDto())
    override suspend fun acceptAlternative(id: String, body: AlternativeAcceptanceRequestDto): Response<AlternativeAcceptanceResponseDto> = Response.success(AlternativeAcceptanceResponseDto())
}

class ItineraryRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeApi: FakeItineraryApi
    private lateinit var repository: ItineraryRepositoryImpl

    @Before
    fun setUp() {
        fakeApi = FakeItineraryApi()
        repository = ItineraryRepositoryImpl(api = fakeApi, ioDispatcher = testDispatcher)
    }

    @Test
    fun testSuccessfulGenerationFlow() = runTest(testDispatcher) {
        val request = ItineraryRequest(
            destinationId = "darjeeling",
            durationDays = 2,
            travelerType = "SOLO",
            pace = "BALANCED",
            interests = listOf("Heritage"),
            budgetLevel = "MODERATE"
        )

        val emissions = repository.generateItinerary(request).toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)

        val success = emissions[1] as Resource.Success
        assertEquals("itin_1", success.data.itineraryId)
        assertEquals("Darjeeling", success.data.destinationName)
        assertEquals(88, success.data.sustainabilityScore)
        assertTrue(success.isLive)
    }

    @Test
    fun testGenerationNetworkFailureEmitsHelpfulOfflineMessage() = runTest(testDispatcher) {
        fakeApi.shouldFailWithNetwork = true

        val request = ItineraryRequest(destinationId = "darjeeling", durationDays = 2)
        val emissions = repository.generateItinerary(request).toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)

        val error = emissions[1] as Resource.Error
        assertTrue(error.message.contains("AI itinerary generation needs an internet connection"))
    }

    @Test
    fun testGenerationTimeoutEmitsTimeoutMessage() = runTest(testDispatcher) {
        fakeApi.shouldFailWithTimeout = true

        val request = ItineraryRequest(destinationId = "darjeeling", durationDays = 2)
        val emissions = repository.generateItinerary(request).toList()

        val error = emissions[1] as Resource.Error
        assertTrue(error.message.contains("Planning took longer than expected"))
    }

    @Test
    fun testGenerationBackendValidation422() = runTest(testDispatcher) {
        fakeApi.httpErrorCode = 422
        fakeApi.httpErrorMessage = "duration_days must be between 1 and 5"

        val request = ItineraryRequest(destinationId = "darjeeling", durationDays = 2)
        val emissions = repository.generateItinerary(request).toList()

        val error = emissions[1] as Resource.Error
        assertTrue(error.message.contains("Backend rejected parameters"))
        assertTrue(error.message.contains("duration_days must be between 1 and 5"))
    }

    @Test
    fun testSuccessfulOptimizationFlow() = runTest(testDispatcher) {
        val initialItinerary = Itinerary(
            itineraryId = "itin_1",
            destinationId = "darjeeling",
            destinationName = "Darjeeling",
            durationDays = 2,
            travelerType = "Solo",
            pace = "Moderate",
            interests = emptyList(),
            totalEstimatedBudgetInr = 2000,
            crowdAvoidanceRating = "Good",
            localEconomicImpactTag = "COMMUNITY",
            days = emptyList(),
            aiGeneratedNote = "",
            sustainabilityScore = 80,
            sustainabilityClassification = "GOOD",
            weatherAdaptationNotice = null,
            whyThisItinerary = emptyList(),
            aiProviderUsed = "rule_based_fallback",
            optimizationHistory = emptyList(),
            isLive = true
        )

        val emissions = repository.optimizeItinerary(
            directive = "AVOID_CROWDS",
            customInstruction = null,
            currentItinerary = initialItinerary
        ).toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)

        val success = emissions[1] as Resource.Success
        assertEquals("itin_1_opt", success.data.itineraryId)
        assertEquals(95, success.data.sustainabilityScore)
        assertEquals("Excellent", success.data.crowdAvoidanceRating)
        assertEquals(listOf("AVOID_CROWDS"), success.data.optimizationHistory)
    }

    @Test
    fun testSessionCacheFallbackWhenNetworkFailsLater() = runTest(testDispatcher) {
        val request = ItineraryRequest(destinationId = "darjeeling", durationDays = 2)

        // 1. First generation succeeds and populates cache
        repository.generateItinerary(request).toList()

        // 2. Second generation encounters offline failure
        fakeApi.shouldFailWithNetwork = true
        val offlineEmissions = repository.generateItinerary(request).toList()

        // Should successfully fall back to cached itinerary labeled with isLive = false
        val result = offlineEmissions.last()
        assertTrue(result is Resource.Success)
        val success = result as Resource.Success
        assertFalse(success.isLive)
        assertEquals("itin_1", success.data.itineraryId)
    }

    @Test
    fun testAuthorityIntegrity() {
        // Assert repository delegates exclusively to API without creating client-side itineraries
        val repoMethods = ItineraryRepositoryImpl::class.java.declaredMethods.map { it.name }
        assertFalse(repoMethods.any { it.contains("callOpenAi", ignoreCase = true) })
        assertFalse(repoMethods.any { it.contains("callClaude", ignoreCase = true) })
        assertFalse(repoMethods.any { it.contains("generateMockItinerary", ignoreCase = true) })
    }
}
