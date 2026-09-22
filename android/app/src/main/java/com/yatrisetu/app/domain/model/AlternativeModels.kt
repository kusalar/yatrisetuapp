package com.yatrisetu.app.domain.model

data class AlternativeWeatherInfo(
    val destinationId: String,
    val temperature: Float,
    val tempMinC: Float? = null,
    val tempMaxC: Float? = null,
    val condition: String,
    val humidity: Int? = null,
    val precipitationChance: Int? = null,
    val provenanceLabel: String = "DEMO MODE",
    val providerMode: String = "DEMO",
    val cacheStatus: String = "LIVE",
    val observedAt: String? = null,
    val temperatureRange: String? = null
)

data class AlternativeDestination(
    val id: String,
    val name: String,
    val tagline: String,
    val state: String = "",
    val heroImage: String,
    val crowdScore: Int,
    val crowdLevel: CrowdLevel,
    val similarityScore: Int,
    val similarityProvenance: String? = null,
    val originalCrowdScore: Int = 0,
    val alternativeCrowdScore: Int = 0,
    val crowdReductionPercent: Int,
    val distanceKm: Float,
    val geographicDistanceKm: Float? = null,
    val roadDistanceKm: Float? = null,
    val distanceProvenance: String = "HAVERSINE_GEOGRAPHIC_ESTIMATE",
    val estimatedCostPerDay: Int,
    val costDifferencePercent: Int,
    val reasonsToRecommend: List<String> = emptyList(),
    val sharedHighlights: List<String> = emptyList(),
    val matchingAttributes: List<String> = emptyList(),
    val keyExperience: String = "",
    val ecoTag: String = "",
    val destinationId: String? = null,
    val currentPressure: Int? = null,
    val expectedPressure: Int? = null,
    val capacityStatus: String = "HEALTHY",
    val availableCapacity: Int? = null,
    val accessStatus: String = "OPEN",
    val weather: AlternativeWeatherInfo? = null,
    val weatherSummary: String? = null,
    val trafficSummary: String? = null,
    val homestayAvailability: String? = null,
    val reasons: List<String> = emptyList(),
    val provenance: String = "REAL — YATRI SETU NETWORK",
    val lastUpdated: String? = null
) {
    val crowdReductionLabel: String
        get() = "$crowdReductionPercent% less crowded"

    val similarityLabel: String
        get() = "$similarityScore% similar"

    val distanceLabel: String
        get() = "${distanceKm.toInt()} km away"

    val dailyCostLabel: String
        get() = "₹$estimatedCostPerDay / day"

    val costSavingsLabel: String
        get() = if (costDifferencePercent > 0) "$costDifferencePercent% lower expense" else ""

    val primaryReason: String
        get() = when {
            reasons.isNotEmpty() -> reasons.first()
            reasonsToRecommend.isNotEmpty() -> reasonsToRecommend.first()
            tagline.isNotBlank() -> tagline
            else -> "Calmer alternative with preserved Himalayan experience"
        }
}

data class AlternativesData(
    val originDestinationId: String,
    val originDestinationName: String,
    val originCrowdScore: Int,
    val originCrowdLevel: CrowdLevel,
    val alternatives: List<AlternativeDestination>,
    val isLive: Boolean = true,
    val cachedAt: Long? = null
) {
    val freshnessLabel: String
        get() = if (isLive) {
            "LIVE · Server Recommendations"
        } else {
            val minutesAgo = cachedAt?.let { (System.currentTimeMillis() - it) / (1000 * 60) } ?: 0
            if (minutesAgo <= 1) "Offline · Cached just now" else "Offline · Cached ${minutesAgo}m ago"
        }
}

data class AlternativeAcceptance(
    val status: String,
    val eventId: String,
    val originalDestinationId: String,
    val alternativeDestinationId: String
)
