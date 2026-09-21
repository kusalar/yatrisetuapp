package com.yatrisetu.app.data.repository

import android.content.Context
import com.yatrisetu.app.data.local.YatriDatabase
import com.yatrisetu.app.data.remote.NetworkClient

object RepositoryProvider {

    @Volatile
    private var destinationRepo: DestinationRepository? = null

    @Volatile
    private var crowdRepo: CrowdRepository? = null

    fun getDestinationRepository(context: Context): DestinationRepository {
        return destinationRepo ?: synchronized(this) {
            destinationRepo ?: run {
                val db = YatriDatabase.getInstance(context)
                DestinationRepositoryImpl(
                    api = NetworkClient.api,
                    destinationDao = db.destinationDao()
                ).also { destinationRepo = it }
            }
        }
    }

    fun getCrowdRepository(context: Context): CrowdRepository {
        return crowdRepo ?: synchronized(this) {
            crowdRepo ?: run {
                val db = YatriDatabase.getInstance(context)
                CrowdRepositoryImpl(
                    api = NetworkClient.api,
                    crowdDao = db.crowdDao()
                ).also { crowdRepo = it }
            }
        }
    }

    fun setDestinationRepositoryForTesting(repository: DestinationRepository?) {
        destinationRepo = repository
    }

    fun setCrowdRepositoryForTesting(repository: CrowdRepository?) {
        crowdRepo = repository
    }
}
