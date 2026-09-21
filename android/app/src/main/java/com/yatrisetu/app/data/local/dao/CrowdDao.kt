package com.yatrisetu.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yatrisetu.app.data.local.entity.CachedCrowdEntity

@Dao
interface CrowdDao {

    @Query("SELECT * FROM cached_crowd WHERE destinationId = :destinationId LIMIT 1")
    suspend fun getCrowdForDestination(destinationId: String): CachedCrowdEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrowd(crowd: CachedCrowdEntity)

    @Query("DELETE FROM cached_crowd WHERE destinationId = :destinationId")
    suspend fun deleteCrowd(destinationId: String)

    @Query("DELETE FROM cached_crowd")
    suspend fun clearAll()
}
