package com.yatrisetu.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yatrisetu.app.data.local.entity.EmergencyContactEntity
import com.yatrisetu.app.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Query("SELECT * FROM saved_trips ORDER BY updatedAt DESC")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM saved_trips WHERE tripId = :tripId LIMIT 1")
    suspend fun getTripById(tripId: String): TripEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Query("DELETE FROM saved_trips WHERE tripId = :tripId")
    suspend fun deleteTrip(tripId: String)
}

@Dao
interface EmergencyContactDao {

    @Query("SELECT * FROM cached_emergency_contacts")
    fun getAllContacts(): Flow<List<EmergencyContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<EmergencyContactEntity>)
}
