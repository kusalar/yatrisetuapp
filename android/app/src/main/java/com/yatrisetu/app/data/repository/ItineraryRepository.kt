package com.yatrisetu.app.data.repository

import com.yatrisetu.app.data.mapper.toDomain
import com.yatrisetu.app.data.mapper.toDto
import com.yatrisetu.app.data.mapper.toResponseDto
import com.yatrisetu.app.data.remote.YatriSetuApi
import com.yatrisetu.app.data.remote.dto.ItineraryOptimizeRequestDto
import com.yatrisetu.app.domain.model.Itinerary
import com.yatrisetu.app.domain.model.ItineraryRequest
import com.yatrisetu.app.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.ConcurrentHashMap

interface ItineraryRepository {
    fun generateItinerary(request: ItineraryRequest): Flow<Resource<Itinerary>>
    
    fun optimizeItinerary(
        directive: String,
        customInstruction: String? = null,
        currentItinerary: Itinerary
    ): Flow<Resource<Itinerary>>

    fun getCachedItinerary(destinationId: String): Itinerary?

    fun clearCache()
}

class ItineraryRepositoryImpl(
    private val api: YatriSetuApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ItineraryRepository {

    // Thread-safe session cache: destinationId -> Pair<Itinerary, CachedTimestamp>
    private val destinationCache = ConcurrentHashMap<String, Pair<Itinerary, Long>>()
    // Thread-safe session cache by itineraryId -> Pair<Itinerary, CachedTimestamp>
    private val itineraryCache = ConcurrentHashMap<String, Pair<Itinerary, Long>>()

    override fun generateItinerary(request: ItineraryRequest): Flow<Resource<Itinerary>> = flow {
        val destId = request.destinationId.lowercase().trim()
        val cachedEntry = destinationCache[destId]
        val cachedItinerary = cachedEntry?.first?.copy(isLive = false, cachedAt = cachedEntry.second)

        // Step 1: Emit loading state
        emit(Resource.Loading(cachedData = cachedItinerary))

        // Step 2: Post generation request to backend
        try {
            val response = api.generateItinerary(request.toDto())
            if (response.isSuccessful && response.body() != null) {
                val now = System.currentTimeMillis()
                val liveItinerary = response.body()!!.toDomain(isLive = true, cachedAt = now)

                // Store in session cache
                destinationCache[destId] = Pair(liveItinerary, now)
                if (liveItinerary.itineraryId.isNotBlank()) {
                    itineraryCache[liveItinerary.itineraryId] = Pair(liveItinerary, now)
                }

                emit(Resource.Success(data = liveItinerary, isLive = true, cachedAt = now))
            } else {
                val code = response.code()
                val errorBodyStr = response.errorBody()?.string().orEmpty()
                val msg = when (code) {
                    422 -> "Backend rejected parameters: ${extractValidationDetail(errorBodyStr)}"
                    404 -> "Destination '${request.destinationId}' was not found for itinerary generation."
                    429 -> "Rate limit reached. Please wait a moment before generating another itinerary."
                    500 -> "Yatri Setu planning engine encountered a temporary error (HTTP 500)."
                    else -> "No itinerary could be generated (HTTP $code)."
                }

                if (cachedItinerary != null) {
                    emit(Resource.Success(data = cachedItinerary, isLive = false, cachedAt = cachedEntry.second))
                } else {
                    emit(Resource.Error(message = msg, cachedData = null))
                }
            }
        } catch (e: Exception) {
            val errorMsg = when (e) {
                is ConnectException, is UnknownHostException ->
                    "AI itinerary generation needs an internet connection."
                is SocketTimeoutException ->
                    "Planning took longer than expected. Please retry."
                else ->
                    "We couldn't reach Yatri Setu. Please check your network."
            }

            if (cachedItinerary != null) {
                emit(Resource.Success(data = cachedItinerary, isLive = false, cachedAt = cachedEntry.second))
            } else {
                emit(Resource.Error(message = errorMsg, cachedData = null, cause = e))
            }
        }
    }.flowOn(ioDispatcher)

    override fun optimizeItinerary(
        directive: String,
        customInstruction: String?,
        currentItinerary: Itinerary
    ): Flow<Resource<Itinerary>> = flow {
        // Emit loading with current itinerary as fallback
        emit(Resource.Loading(cachedData = currentItinerary))

        try {
            val requestDto = ItineraryOptimizeRequestDto(
                itineraryId = currentItinerary.itineraryId,
                destinationId = currentItinerary.destinationId.lowercase().trim(),
                instruction = directive,
                customInstruction = customInstruction?.ifBlank { null },
                currentItinerary = currentItinerary.toResponseDto()
            )

            val response = api.optimizeItinerary(requestDto)
            if (response.isSuccessful && response.body() != null) {
                val now = System.currentTimeMillis()
                val optimizedItinerary = response.body()!!.toDomain(isLive = true, cachedAt = now)

                // Update session caches
                val destId = optimizedItinerary.destinationId.lowercase().trim()
                destinationCache[destId] = Pair(optimizedItinerary, now)
                if (optimizedItinerary.itineraryId.isNotBlank()) {
                    itineraryCache[optimizedItinerary.itineraryId] = Pair(optimizedItinerary, now)
                }

                emit(Resource.Success(data = optimizedItinerary, isLive = true, cachedAt = now))
            } else {
                val code = response.code()
                val errorBodyStr = response.errorBody()?.string().orEmpty()
                val msg = when (code) {
                    422 -> "Optimization parameters invalid: ${extractValidationDetail(errorBodyStr)}"
                    429 -> "Optimization rate limit reached. Please wait a moment."
                    else -> "Unable to optimize itinerary (HTTP $code)."
                }
                emit(Resource.Error(message = msg, cachedData = currentItinerary))
            }
        } catch (e: Exception) {
            val errorMsg = when (e) {
                is ConnectException, is UnknownHostException ->
                    "AI itinerary optimization needs an internet connection."
                is SocketTimeoutException ->
                    "Optimization took longer than expected. Please retry."
                else ->
                    "We couldn't reach Yatri Setu to optimize your trip."
            }
            emit(Resource.Error(message = errorMsg, cachedData = currentItinerary, cause = e))
        }
    }.flowOn(ioDispatcher)

    override fun getCachedItinerary(destinationId: String): Itinerary? {
        val entry = destinationCache[destinationId.lowercase().trim()] ?: return null
        return entry.first.copy(isLive = false, cachedAt = entry.second)
    }

    override fun clearCache() {
        destinationCache.clear()
        itineraryCache.clear()
    }

    private fun extractValidationDetail(errorBody: String): String {
        return if (errorBody.contains("\"detail\":")) {
            val match = Regex("\"detail\"\\s*:\\s*\"([^\"]+)\"").find(errorBody)
                ?: Regex("\"msg\"\\s*:\\s*\"([^\"]+)\"").find(errorBody)
            match?.groupValues?.get(1) ?: errorBody.take(120)
        } else {
            errorBody.take(120)
        }
    }
}
