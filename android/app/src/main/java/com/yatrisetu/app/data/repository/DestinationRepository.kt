package com.yatrisetu.app.data.repository

import com.yatrisetu.app.data.local.dao.DestinationDao
import com.yatrisetu.app.data.mapper.toDomain
import com.yatrisetu.app.data.mapper.toEntity
import com.yatrisetu.app.data.remote.YatriSetuApi
import com.yatrisetu.app.domain.model.Destination
import com.yatrisetu.app.domain.model.DestinationSummary
import com.yatrisetu.app.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface DestinationRepository {
    fun getDestinations(query: String? = null, forceRefresh: Boolean = false): Flow<Resource<List<DestinationSummary>>>
    fun getDestinationDetails(id: String, forceRefresh: Boolean = false): Flow<Resource<Destination>>
}

class DestinationRepositoryImpl(
    private val api: YatriSetuApi,
    private val destinationDao: DestinationDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DestinationRepository {

    override fun getDestinations(
        query: String?,
        forceRefresh: Boolean
    ): Flow<Resource<List<DestinationSummary>>> = flow {
        // Step 1: Read local Room cache for instant response
        val cachedEntities = destinationDao.getAllDestinationsList()
        val cachedDomain = cachedEntities.map { it.toDomain() }

        if (cachedDomain.isNotEmpty()) {
            emit(Resource.Loading(cachedData = filterDestinations(cachedDomain, query)))
        } else {
            emit(Resource.Loading(cachedData = null))
        }

        // Step 2: Try fetching fresh data from backend
        try {
            val response = api.getDestinations(query = query)
            if (response.isSuccessful && response.body() != null) {
                val dtoList = response.body()!!
                val freshDomain = dtoList.map { it.toDomain(isLive = true) }

                // Update local Room cache
                val now = System.currentTimeMillis()
                val entitiesToCache = freshDomain.map { it.toEntity(cachedAt = now) }
                destinationDao.insertDestinations(entitiesToCache)

                emit(Resource.Success(data = freshDomain, isLive = true, cachedAt = now))
            } else {
                val errorMsg = "Server error ${response.code()}: ${response.message()}"
                if (cachedDomain.isNotEmpty()) {
                    emit(
                        Resource.Success(
                            data = filterDestinations(cachedDomain, query),
                            isLive = false,
                            cachedAt = cachedEntities.firstOrNull()?.cachedAt
                        )
                    )
                } else {
                    emit(Resource.Error(message = errorMsg, cachedData = null))
                }
            }
        } catch (e: Exception) {
            // Step 3: Network failure, timeout, or connection refused -> Fallback to Room
            val userFriendlyMessage = when {
                e is java.net.ConnectException || e is java.net.UnknownHostException ->
                    "Unable to connect to Yatri Setu server. Showing offline information."
                e is java.net.SocketTimeoutException ->
                    "Connection timed out. Showing offline information."
                else -> "Unable to refresh destination data. Showing offline information."
            }

            if (cachedDomain.isNotEmpty()) {
                emit(
                    Resource.Success(
                        data = filterDestinations(cachedDomain, query),
                        isLive = false,
                        cachedAt = cachedEntities.firstOrNull()?.cachedAt
                    )
                )
            } else {
                emit(Resource.Error(message = userFriendlyMessage, cachedData = null, cause = e))
            }
        }
    }.flowOn(ioDispatcher)

    override fun getDestinationDetails(
        id: String,
        forceRefresh: Boolean
    ): Flow<Resource<Destination>> = flow {
        emit(Resource.Loading(cachedData = null))

        try {
            val response = api.getDestinationDetails(id)
            if (response.isSuccessful && response.body() != null) {
                val domain = response.body()!!.toDomain(isLive = true)
                emit(Resource.Success(data = domain, isLive = true))
            } else {
                emit(Resource.Error(message = "Destination details unavailable (${response.code()})"))
            }
        } catch (e: Exception) {
            val errorMsg = when {
                e is java.net.ConnectException || e is java.net.UnknownHostException ->
                    "Unable to reach server. Please check your connection."
                else -> "Unable to load destination details."
            }
            emit(Resource.Error(message = errorMsg, cause = e))
        }
    }.flowOn(ioDispatcher)

    private fun filterDestinations(list: List<DestinationSummary>, query: String?): List<DestinationSummary> {
        if (query.isNullOrBlank()) return list
        val q = query.trim().lowercase()
        return list.filter {
            it.name.lowercase().contains(q) ||
                it.region.lowercase().contains(q) ||
                it.state.lowercase().contains(q) ||
                it.tags.any { tag -> tag.lowercase().contains(q) }
        }
    }
}
