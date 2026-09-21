package com.yatrisetu.app.domain.model

data class DestinationSummary(
    val id: String,
    val name: String,
    val tagline: String,
    val region: String,
    val state: String,
    val heroImage: String,
    val crowdScore: Int,
    val crowdLevel: CrowdLevel,
    val avgCostPerDayInr: Int,
    val tags: List<String>,
    val isLive: Boolean = true,
    val cachedAt: Long? = null
) {
    val freshnessLabel: String
        get() = if (isLive) {
            "LIVE"
        } else {
            val minutesAgo = cachedAt?.let { (System.currentTimeMillis() - it) / (1000 * 60) } ?: 0
            if (minutesAgo <= 1) "Cached just now" else "Cached ${minutesAgo}m ago"
        }
}

data class DestinationAttributes(
    val nature: Float,
    val climate: String,
    val activities: List<String>,
    val culture: String,
    val budgetLevel: String,
    val avgCostPerDayInr: Int,
    val accessibility: String,
    val altitudeFt: Int
)

data class Attraction(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val crowdDensity: String,
    val visitDurationHrs: Float,
    val bestTime: String,
    val imageUrl: String
)

data class Destination(
    val id: String,
    val name: String,
    val tagline: String,
    val region: String,
    val state: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val heroImage: String,
    val galleryImages: List<String>,
    val attributes: DestinationAttributes,
    val highlights: List<String>,
    val attractions: List<Attraction>,
    val baseCrowdScore: Int,
    val recommendedDurationDays: Int = 2,
    val tags: List<String>,
    val isLive: Boolean = true,
    val cachedAt: Long? = null
)
