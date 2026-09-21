package com.yatrisetu.app.data.repository

import com.yatrisetu.app.data.local.dao.CrowdDao
import com.yatrisetu.app.data.mapper.toDomain
import com.yatrisetu.app.data.mapper.toEntity
import com.yatrisetu.app.data.remote.YatriSetuApi
import com.yatrisetu.app.domain.model.CrowdInfo
import com.yatrisetu.app.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface CrowdRepository {
    fun getCrowdInfo(destinationId: String, forceRefresh: Boolean = false): Flow<Resource<CrowdInfo>>
}

class CrowdRepositoryImpl(
    private val api: YatriSetuApi,
    private val crowdDao: CrowdDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CrowdRepository {

    override fun getCrowdInfo(
        destinationId: String,
        forceRefresh: Boolean
    ): Flow<Resource<CrowdInfo>> = flow {
        // Step 1: Read Room cache for instant feedback
        val cachedEntity = crowdDao.getCrowdForDestination(destinationId)
        val cachedDomain = cachedEntity?.toDomain()

        if (cachedDomain != null) {
            emit(Resource.Loading(cachedData = cachedDomain))
        } else {
            emit(Resource.Loading(cachedData = null))
        }

        // Step 2: Request canonical live crowd intelligence from Yatri Setu backend
        try {
            val response = api.getDestinationCrowd(destinationId)
            if (response.isSuccessful && response.body() != null) {
                val dtoList = response.body()!!
                val now = System.currentTimeMillis()
                val freshDomain = dtoList.toDomain(isLive = true, cachedAt = now)

                // Persist fresh canonical crowd intelligence to Room cache
                crowdDao.insertCrowd(freshDomain.toEntity(cachedAt = now))

                emit(Resource.Success(data = freshDomain, isLive = true, cachedAt = now))
            } else {
                val code = response.code()
                val msg = "Crowd intelligence unavailable (HTTP $code)"
                if (cachedDomain != null) {
                    emit(Resource.Success(data = cachedDomain, isLive = false, cachedAt = cachedEntity.cachedAt))
                } else {
                    emit(Resource.Error(message = msg, cachedData = null))
                }
            }
        } catch (e: Exception) {
            // Step 3: Handle network loss, timeout, or unreachable server
            val errorMsg = when {
                e is java.net.ConnectException || e is java.net.UnknownHostException ->
                    "Unable to connect to live crowd server. Showing saved local snapshot."
                e is java.net.SocketTimeoutException ->
                    "Crowd telemetry timed out. Showing saved local snapshot."
                else -> "Unable to refresh crowd telemetry. Showing saved snapshot."
            }

            if (cachedDomain != null) {
                emit(Resource.Success(data = cachedDomain, isLive = false, cachedAt = cachedEntity.cachedAt))
            } else {
                emit(Resource.Error(message = errorMsg, cachedData = null, cause = e))
            }
        }
    }.flowOn(ioDispatcher)
}
