package com.yatrisetu.app.domain.model

data class AlternativeWeatherInfo(
    val destinationId: String,
    val temperature: Float,
    val condition: String,
    val provenanceLabel: String
)

data class AlternativeDestination(
    val id: String,
    val name: String,
    val tagline: String,
    val heroImage: String,
    val crowdScore: Int,
    val crowdLevel: CrowdLevel,
    val similarityScore: Int,
    val crowdReductionPercent: Int,
    val distanceKm: Float,
    val estimatedCostPerDay: Int,
    val costDifferencePercent: Int,
    val reasonsToRecommend: List<String>,
    val sharedHighlights: List<String>,
    val capacityStatus: String,
    val accessStatus: String,
    val weather: AlternativeWeatherInfo?
)
