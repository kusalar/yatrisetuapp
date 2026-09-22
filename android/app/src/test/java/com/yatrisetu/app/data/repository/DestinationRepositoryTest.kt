package com.yatrisetu.app.data.repository

import com.yatrisetu.app.data.local.dao.DestinationDao
import com.yatrisetu.app.data.local.entity.DestinationEntity
import com.yatrisetu.app.data.remote.YatriSetuApi
import com.yatrisetu.app.data.remote.dto.AlternativesResponseDto
import com.yatrisetu.app.data.remote.dto.CrowdResponseDto
import com.yatrisetu.app.data.remote.dto.DestinationDetailDto
import com.yatrisetu.app.data.remote.dto.DestinationSummaryDto
import com.yatrisetu.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.net.ConnectException

class FakeDestinationDao : DestinationDao {
    private val storage = mutableListOf<DestinationEntity>()

    override fun getAllDestinations(): Flow<List<DestinationEntity>> = flowOf(storage)

    override suspend fun getAllDestinationsList(): List<DestinationEntity> = storage.toList()

    override suspend fun getDestinationById(id: String): DestinationEntity? = storage.find { it.id == id }

    override suspend fun insertDestinations(destinations: List<DestinationEntity>) {
        storage.removeAll { existing -> destinations.any { it.id == existing.id } }
        storage.addAll(destinations)
    }

    override suspend fun clearAll() {
        storage.clear()
    }
}

class FakeYatriSetuApi(
    var shouldFail: Boolean = false,
    var mockDestinations: List<DestinationSummaryDto> = emptyList()
) : YatriSetuApi {

    override suspend fun getDestinations(
        query: String?,
        crowdLevel: String?
    ): Response<List<DestinationSummaryDto>> {
        if (shouldFail) {
            throw ConnectException("Connection refused")
        }
        return Response.success(mockDestinations)
    }

    override suspend fun getDestinationDetails(id: String): Response<DestinationDetailDto> {
        if (shouldFail) throw ConnectException("Connection refused")
        return Response.success(DestinationDetailDto(id = id, name = "Test $id"))
    }

    override suspend fun getDestinationCrowd(id: String): Response<CrowdResponseDto> {
        if (shouldFail) throw ConnectException("Connection refused")
        return Response.success(CrowdResponseDto(destinationId = id, destinationName = "Test $id", crowdScore = 50))
    }

    override suspend fun getDestinationAlternatives(id: String): Response<AlternativesResponseDto> {
        if (shouldFail) throw ConnectException("Connection refused")
        return Response.success(AlternativesResponseDto())
    }

    override suspend fun acceptAlternative(
        id: String,
        body: com.yatrisetu.app.data.remote.dto.AlternativeAcceptanceRequestDto
    ): Response<com.yatrisetu.app.data.remote.dto.AlternativeAcceptanceResponseDto> {
        if (shouldFail) throw ConnectException("Connection refused")
        return Response.success(com.yatrisetu.app.data.remote.dto.AlternativeAcceptanceResponseDto())
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

class DestinationRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun testSuccessfulApiFetchUpdatesCacheAndEmitsLive() = runTest(testDispatcher) {
        val fakeDao = FakeDestinationDao()
        val fakeApi = FakeYatriSetuApi(
            shouldFail = false,
            mockDestinations = listOf(
                DestinationSummaryDto(
                    id = "darjeeling",
                    name = "Darjeeling",
                    crowdScore = 88,
                    crowdLevel = "VERY HIGH"
                )
            )
        )

        val repository = DestinationRepositoryImpl(fakeApi, fakeDao, testDispatcher)

        val emissions = repository.getDestinations().toList()

        // Verify emissions: Loading -> Success(Live)
        assertTrue(emissions.first() is Resource.Loading)
        val success = emissions.last() as Resource.Success
        assertEquals(1, success.data.size)
        assertEquals("darjeeling", success.data[0].id)
        assertTrue(success.isLive)

        // Verify Room cache was updated
        assertEquals(1, fakeDao.getAllDestinationsList().size)
        assertEquals("darjeeling", fakeDao.getAllDestinationsList()[0].id)
    }

    @Test
    fun testNetworkFailureFallsBackToRoomCacheAsStale() = runTest(testDispatcher) {
        val fakeDao = FakeDestinationDao()
        // Pre-populate Room cache
        fakeDao.insertDestinations(
            listOf(
                DestinationEntity(
                    id = "kalimpong",
                    name = "Kalimpong",
                    tagline = "Ridge flower haven",
                    region = "Eastern Himalayas",
                    state = "West Bengal",
                    heroImage = "",
                    crowdScore = 42,
                    crowdLevel = "MEDIUM",
                    avgCostPerDayInr = 2800,
                    cachedAt = 1000L
                )
            )
        )

        val fakeApi = FakeYatriSetuApi(shouldFail = true)
        val repository = DestinationRepositoryImpl(fakeApi, fakeDao, testDispatcher)

        val emissions = repository.getDestinations().toList()

        // Emits Loading(with cached data) -> Success(with cached data, isLive = false)
        val last = emissions.last() as Resource.Success
        assertEquals(1, last.data.size)
        assertEquals("kalimpong", last.data[0].id)
        assertFalse(last.isLive)
        assertEquals(1000L, last.cachedAt)
    }

    @Test
    fun testNetworkFailureWithoutCacheEmitsError() = runTest(testDispatcher) {
        val fakeDao = FakeDestinationDao()
        val fakeApi = FakeYatriSetuApi(shouldFail = true)
        val repository = DestinationRepositoryImpl(fakeApi, fakeDao, testDispatcher)

        val emissions = repository.getDestinations().toList()

        val last = emissions.last() as Resource.Error
        assertTrue(last.message.contains("Unable to connect"))
        assertEquals(null, last.cachedData)
    }
}
