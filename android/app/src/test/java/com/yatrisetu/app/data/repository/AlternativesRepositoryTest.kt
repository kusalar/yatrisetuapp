package com.yatrisetu.app.data.repository

import com.yatrisetu.app.data.remote.YatriSetuApi
import com.yatrisetu.app.data.remote.dto.AlternativeAcceptanceRequestDto
import com.yatrisetu.app.data.remote.dto.AlternativeAcceptanceResponseDto
import com.yatrisetu.app.data.remote.dto.AlternativeRecommendationDto
import com.yatrisetu.app.data.remote.dto.AlternativesResponseDto
import com.yatrisetu.app.data.remote.dto.CrowdResponseDto
import com.yatrisetu.app.data.remote.dto.DestinationDetailDto
import com.yatrisetu.app.data.remote.dto.DestinationSummaryDto
import com.yatrisetu.app.util.Resource
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.net.ConnectException

class FakeAlternativesYatriSetuApi : YatriSetuApi {
    var shouldFail: Boolean = false
    var mockAlternativesResponse: AlternativesResponseDto = AlternativesResponseDto()
    var mockAcceptanceResponse: AlternativeAcceptanceResponseDto = AlternativeAcceptanceResponseDto()
    var lastAcceptanceRequest: AlternativeAcceptanceRequestDto? = null

    override suspend fun getDestinations(query: String?, crowdLevel: String?): Response<List<DestinationSummaryDto>> {
        return Response.success(emptyList())
    }

    override suspend fun getDestinationDetails(id: String): Response<DestinationDetailDto> {
        return Response.success(DestinationDetailDto(id = id, name = "Test"))
    }

    override suspend fun getDestinationCrowd(id: String): Response<CrowdResponseDto> {
        return Response.success(CrowdResponseDto(destinationId = id, destinationName = "Test"))
    }

    override suspend fun getDestinationAlternatives(id: String): Response<AlternativesResponseDto> {
        if (shouldFail) throw ConnectException("Connection refused")
        return Response.success(mockAlternativesResponse)
    }

    override suspend fun acceptAlternative(
        id: String,
        body: AlternativeAcceptanceRequestDto
    ): Response<AlternativeAcceptanceResponseDto> {
        if (shouldFail) throw ConnectException("Connection refused")
        lastAcceptanceRequest = body
        return Response.success(mockAcceptanceResponse)
    }

    override suspend fun generateItinerary(
        request: com.yatrisetu.app.data.remote.dto.ItineraryRequestDto
    ): Response<com.yatrisetu.app.data.remote.dto.ItineraryResponseDto> {
        if (shouldFail) throw ConnectException("Connection refused")
        return Response.success(com.yatrisetu.app.data.remote.dto.ItineraryResponseDto())
    }

    override suspend fun optimizeItinerary(
        request: com.yatrisetu.app.data.remote.dto.ItineraryOptimizeRequestDto
    ): Response<com.yatrisetu.app.data.remote.dto.ItineraryResponseDto> {
        if (shouldFail) throw ConnectException("Connection refused")
        return Response.success(com.yatrisetu.app.data.remote.dto.ItineraryResponseDto())
    }
}

class AlternativesRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun testSuccessfulAlternativesFetchEmitsLive() = runTest(testDispatcher) {
        val fakeApi = FakeAlternativesYatriSetuApi().apply {
            mockAlternativesResponse = AlternativesResponseDto(
                originDestinationId = "darjeeling",
                originDestinationName = "Darjeeling",
                originCrowdScore = 88,
                originCrowdLevel = "VERY HIGH",
                alternatives = listOf(
                    AlternativeRecommendationDto(
                        id = "kalimpong",
                        name = "Kalimpong",
                        crowdScore = 35,
                        crowdLevel = "LOW",
                        similarityScore = 88,
                        crowdReductionPercent = 60,
                        distanceKm = 52f,
                        estimatedCostPerDay = 2500,
                        costDifferencePercent = 35
                    )
                )
            )
        }

        val repository = AlternativesRepositoryImpl(fakeApi, testDispatcher)
        val emissions = repository.getAlternatives("darjeeling").toList()

        // 1. Loading
        assertTrue(emissions.first() is Resource.Loading)

        // 2. Success with Live data
        val success = emissions.last() as Resource.Success
        assertEquals("darjeeling", success.data.originDestinationId)
        assertEquals(1, success.data.alternatives.size)
        assertEquals("kalimpong", success.data.alternatives[0].id)
        assertTrue(success.isLive)
        assertTrue(success.data.isLive)
    }

    @Test
    fun testNetworkFailureFallsBackToCachedRecommendations() = runTest(testDispatcher) {
        val fakeApi = FakeAlternativesYatriSetuApi().apply {
            mockAlternativesResponse = AlternativesResponseDto(
                originDestinationId = "darjeeling",
                originDestinationName = "Darjeeling",
                originCrowdScore = 88,
                originCrowdLevel = "VERY HIGH",
                alternatives = listOf(
                    AlternativeRecommendationDto(
                        id = "kalimpong",
                        name = "Kalimpong",
                        crowdScore = 35,
                        crowdLevel = "LOW",
                        similarityScore = 88,
                        crowdReductionPercent = 60,
                        distanceKm = 52f
                    )
                )
            )
        }

        val repository = AlternativesRepositoryImpl(fakeApi, testDispatcher)

        // First successful call populates session cache
        val firstEmissions = repository.getAlternatives("darjeeling").toList()
        assertTrue((firstEmissions.last() as Resource.Success).isLive)

        // Now simulate network failure
        fakeApi.shouldFail = true
        val secondEmissions = repository.getAlternatives("darjeeling").toList()

        val last = secondEmissions.last() as Resource.Success
        assertEquals(1, last.data.alternatives.size)
        assertEquals("kalimpong", last.data.alternatives[0].id)
        // Must NOT pretend to be live!
        assertFalse(last.isLive)
        assertFalse(last.data.isLive)
        assertTrue(last.data.freshnessLabel.contains("Offline"))
    }

    @Test
    fun testNetworkFailureWithoutCacheEmitsError() = runTest(testDispatcher) {
        val fakeApi = FakeAlternativesYatriSetuApi().apply {
            shouldFail = true
        }

        val repository = AlternativesRepositoryImpl(fakeApi, testDispatcher)
        val emissions = repository.getAlternatives("darjeeling").toList()

        val last = emissions.last() as Resource.Error
        assertTrue(last.message.contains("Unable to connect"))
        assertEquals(null, last.cachedData)
    }

    @Test
    fun testAcceptAlternativeRecordsEvent() = runTest(testDispatcher) {
        val fakeApi = FakeAlternativesYatriSetuApi().apply {
            mockAcceptanceResponse = AlternativeAcceptanceResponseDto(
                status = "recorded",
                eventId = "event-xyz",
                originalDestinationId = "darjeeling",
                alternativeDestinationId = "kalimpong"
            )
        }

        val repository = AlternativesRepositoryImpl(fakeApi, testDispatcher)
        val result = repository.acceptAlternative(
            originId = "darjeeling",
            alternativeId = "kalimpong",
            similarityScore = 88
        )

        assertTrue(result.isSuccess)
        val acceptance = result.getOrThrow()
        assertEquals("recorded", acceptance.status)
        assertEquals("event-xyz", acceptance.eventId)
        assertEquals("kalimpong", fakeApi.lastAcceptanceRequest?.alternativeDestinationId)
    }
}
