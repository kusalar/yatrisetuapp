package com.yatrisetu.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_crowd")
data class CachedCrowdEntity(
    @PrimaryKey val destinationId: String,
    val destinationName: String,
    val crowdScore: Int,
    val crowdLevel: String,
    val colorCode: String,
    val summary: String,
    val whyCrowded: String,
    val bottlenecks: String,
    val factorsJson: String,
    val liveTrafficStatus: String,
    val hotelOccupancyRate: String,
    val lastUpdated: String,
    val peakVisitingHours: String = "",
    val bestTimeToVisitToday: String = "",
    val provenanceLabel: String? = null,
    val cachedAt: Long = System.currentTimeMillis()
)
