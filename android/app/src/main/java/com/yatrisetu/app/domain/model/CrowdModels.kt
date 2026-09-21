package com.yatrisetu.app.domain.model

enum class CrowdLevel(val label: String) {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
    VERY_HIGH("VERY HIGH");

    val displayName: String
        get() = when (this) {
            LOW -> "LOW"
            MEDIUM -> "MODERATE"
            HIGH -> "HIGH"
            VERY_HIGH -> "CRITICAL"
        }

    companion object {
        fun fromString(value: String?): CrowdLevel {
            return when (value?.uppercase()?.trim()) {
                "LOW" -> LOW
                "MEDIUM", "MODERATE" -> MEDIUM
                "HIGH" -> HIGH
                "VERY HIGH", "VERY_HIGH", "CRITICAL" -> VERY_HIGH
                else -> MEDIUM
            }
        }

        fun fromScore(score: Int): CrowdLevel {
            return when {
                score < 40 -> LOW
                score in 40..59 -> MEDIUM
                score in 60..79 -> HIGH
                else -> VERY_HIGH
            }
        }
    }
}

data class CrowdFactor(
    val name: String,
    val key: String,
    val rawValue: Float,
    val weightPercentage: Int,
    val weightedContribution: Float,
    val description: String
)

data class CrowdInfo(
    val destinationId: String,
    val destinationName: String,
    val crowdScore: Int,
    val crowdLevel: CrowdLevel,
    val colorCode: String = "#D97706",
    val summary: String,
    val whyCrowded: List<String> = emptyList(),
    val bottlenecks: List<String> = emptyList(),
    val factors: List<CrowdFactor> = emptyList(),
    val liveTrafficStatus: String = "",
    val hotelOccupancyRate: String = "",
    val lastUpdated: String = "",
    val peakVisitingHours: String = "",
    val bestTimeToVisitToday: String = "",
    val provenanceLabel: String? = null,
    val providerMode: String? = null,
    val dataQuality: String? = null,
    val pressureScore: Float? = null,
    val pressureLevel: String? = null,
    val confidencePercent: Int? = null,
    val carryingCapacityPercent: Float? = null,
    val advisory: String? = null,
    val recommendedAction: String? = null,
    val isLive: Boolean = true,
    val cachedAt: Long? = null
) {
    val freshnessLabel: String
        get() = if (isLive) {
            "LIVE · Real-time Telemetry"
        } else {
            val minutesAgo = cachedAt?.let { (System.currentTimeMillis() - it) / (1000 * 60) } ?: 0
            if (minutesAgo <= 1) "Offline · Cached just now" else "Offline · Cached ${minutesAgo}m ago"
        }

    val pressureDrivers: List<CrowdFactor>
        get() = factors
}
