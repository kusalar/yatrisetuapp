package com.yatrisetu.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_trips")
data class TripEntity(
    @PrimaryKey val tripId: String,
    val destinationId: String,
    val destinationName: String,
    val homestayName: String,
    val dates: String,
    val status: String,
    val travelersCount: Int,
    val digitalPassCode: String,
    val qrPayload: String,
    val weatherAlert: String,
    val hostSupportNumber: String,
    val checkInLocation: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_emergency_contacts")
data class EmergencyContactEntity(
    @PrimaryKey val phoneNumber: String,
    val serviceName: String,
    val description: String,
    val jurisdiction: String
)
