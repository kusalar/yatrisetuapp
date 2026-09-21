package com.yatrisetu.app.domain.model

data class HostProfile(
    val name: String,
    val avatarUrl: String,
    val experienceYears: Int,
    val languages: List<String>,
    val about: String,
    val verifiedPanchayat: Boolean
)

data class Homestay(
    val id: String,
    val destinationId: String,
    val destinationName: String,
    val title: String,
    val tagline: String,
    val address: String,
    val pricePerNightInr: Int,
    val rating: Float,
    val reviewsCount: Int,
    val roomType: String,
    val maxGuests: Int,
    val amenities: List<String>,
    val images: List<String>,
    val host: HostProfile,
    val panchayatFundPercent: Int = 5,
    val hostEarningPercent: Int = 90,
    val verified: Boolean = true
)
