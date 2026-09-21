package com.yatrisetu.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CrowdFactorDto(
    @SerialName("name") val name: String = "",
    @SerialName("key") val key: String = "",
    @SerialName("raw_value") val rawValue: Float = 0f,
    @SerialName("weight_percentage") val weightPercentage: Int = 0,
    @SerialName("weighted_contribution") val weightedContribution: Float = 0f,
    @SerialName("description") val description: String = ""
)

@Serializable
data class CrowdResponseDto(
    @SerialName("destination_id") val destinationId: String = "",
    @SerialName("destination_name") val destinationName: String = "",
    @SerialName("crowd_score") val crowdScore: Int = 0,
    @SerialName("crowd_level") val crowdLevel: String = "MEDIUM",
    @SerialName("color_code") val colorCode: String = "#D97706",
    @SerialName("summary") val summary: String = "",
    @SerialName("why_crowded") val whyCrowded: List<String> = emptyList(),
    @SerialName("bottlenecks") val bottlenecks: List<String> = emptyList(),
    @SerialName("peak_visiting_hours") val peakVisitingHours: String = "",
    @SerialName("best_time_to_visit_today") val bestTimeToVisitToday: String = "",
    @SerialName("factors") val factors: List<CrowdFactorDto> = emptyList(),
    @SerialName("live_traffic_status") val liveTrafficStatus: String = "",
    @SerialName("hotel_occupancy_rate") val hotelOccupancyRate: String = "",
    @SerialName("last_updated") val lastUpdated: String = "",
    @SerialName("provenance_label") val provenanceLabel: String? = null,
    @SerialName("provider_mode") val providerMode: String? = null,
    @SerialName("data_quality") val dataQuality: String? = null,
    @SerialName("pressure_score") val pressureScore: Float? = null,
    @SerialName("pressure_level") val pressureLevel: String? = null,
    @SerialName("confidence_percent") val confidencePercent: Int? = null,
    @SerialName("carrying_capacity_percent") val carryingCapacityPercent: Float? = null,
    @SerialName("advisory") val advisory: String? = null,
    @SerialName("recommended_action") val recommendedAction: String? = null
)
