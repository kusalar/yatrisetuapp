package com.yatrisetu.app.data.repository

import com.yatrisetu.app.data.mapper.toDomain
import com.yatrisetu.app.data.remote.YatriSetuApi
import com.yatrisetu.app.data.remote.dto.AlternativeAcceptanceRequestDto
import com.yatrisetu.app.domain.model.AlternativeAcceptance
import com.yatrisetu.app.domain.model.AlternativesData
import com.yatrisetu.app.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.ConcurrentHashMap

interface AlternativesRepository {
    fun getAlternatives(originId: String, forceRefresh: Boolean = false): Flow<Resource<AlternativesData>>

    suspend fun acceptAlternative(
        originId: String,
        alternativeId: String,
        similarityScore: Int? = null,
        sessionId: String? = null
    ): Result<AlternativeAcceptance>
}

class AlternativesRepositoryImpl(
    private val api: YatriSetuApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AlternativesRepository {

    // Thread-safe session cache storing recent backend alternative recommendations
    private val cache = ConcurrentHashMap<String, Pair<AlternativesData, Long>>()

    override fun getAlternatives(
        originId: String,
        forceRefresh: Boolean
    ): Flow<Resource<AlternativesData>> = flow {
        val normId = originId.lowercase().trim()
        val cachedEntry = cache[normId]
        val cachedData = cachedEntry?.first?.copy(isLive = false, cachedAt = cachedEntry.second)

        // Step 1: Emit loading state with cached snapshot if available
        emit(Resource.Loading(cachedData = cachedData))

        // Step 2: Fetch fresh, authoritative alternative recommendations from backend
        try {
            val response = api.getDestinationAlternatives(normId)
            if (response.isSuccessful && response.body() != null) {
                val now = System.currentTimeMillis()
                val liveData = response.body()!!.toDomain(isLive = true, cachedAt = now)

                // Update in-memory session cache
                cache[normId] = Pair(liveData, now)

                emit(Resource.Success(data = liveData, isLive = true, cachedAt = now))
            } else {
                val code = response.code()
                val msg = "Flow alternatives unavailable (HTTP $code)"
                if (cachedData != null) {
                    emit(Resource.Success(data = cachedData, isLive = false, cachedAt = cachedEntry.second))
                } else {
                    emit(Resource.Error(message = msg, cachedData = null))
                }
            }
        } catch (e: Exception) {
            // Step 3: Network loss or server offline -> safely fallback to cached recommendation
            val errorMsg = when {
                e is java.net.ConnectException || e is java.net.UnknownHostException ->
                    "Unable to connect to Yatri Setu server. Showing offline recommendations."
                e is java.net.SocketTimeoutException ->
                    "Flow Advisor request timed out. Showing offline recommendations."
                else -> "Unable to refresh flow alternatives. Showing offline recommendations."
            }

            if (cachedData != null) {
                emit(Resource.Success(data = cachedData, isLive = false, cachedAt = cachedEntry.second))
            } else {
                emit(Resource.Error(message = errorMsg, cachedData = null, cause = e))
            }
        }
    }.flowOn(ioDispatcher)

    override suspend fun acceptAlternative(
        originId: String,
        alternativeId: String,
        similarityScore: Int?,
        sessionId: String?
    ): Result<AlternativeAcceptance> {
        return try {
            val response = api.acceptAlternative(
                id = originId.lowercase().trim(),
                body = AlternativeAcceptanceRequestDto(
                    alternativeDestinationId = alternativeId.lowercase().trim(),
                    originalDestinationId = originId.lowercase().trim(),
                    similarityScore = similarityScore,
                    sessionId = sessionId
                )
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Failed to record acceptance: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
