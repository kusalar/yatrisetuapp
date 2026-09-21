package com.yatrisetu.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_destinations")
data class DestinationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val tagline: String,
    val region: String,
    val state: String,
    val heroImage: String,
    val crowdScore: Int,
    val crowdLevel: String,
    val avgCostPerDayInr: Int,
    val tags: String = "",
    val cachedAt: Long = System.currentTimeMillis()
)
