package com.yatrisetu.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yatrisetu.app.data.local.entity.DestinationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DestinationDao {

    @Query("SELECT * FROM cached_destinations ORDER BY name ASC")
    fun getAllDestinations(): Flow<List<DestinationEntity>>

    @Query("SELECT * FROM cached_destinations ORDER BY name ASC")
    suspend fun getAllDestinationsList(): List<DestinationEntity>

    @Query("SELECT * FROM cached_destinations WHERE id = :id LIMIT 1")
    suspend fun getDestinationById(id: String): DestinationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDestinations(destinations: List<DestinationEntity>)

    @Query("DELETE FROM cached_destinations")
    suspend fun clearAll()
}
