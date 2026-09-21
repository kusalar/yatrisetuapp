package com.yatrisetu.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DestinationSummaryDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("tagline") val tagline: String = "",
    @SerialName("region") val region: String = "",
    @SerialName("state") val state: String = "",
    @SerialName("hero_image") val heroImage: String = "",
    @SerialName("crowd_score") val crowdScore: Int = 0,
    @SerialName("crowd_level") val crowdLevel: String = "MEDIUM",
    @SerialName("avg_cost_per_day_inr") val avgCostPerDayInr: Int = 0,
    @SerialName("tags") val tags: List<String> = emptyList(),
    @SerialName("distance_from_query_km") val distanceFromQueryKm: Float? = null
)

@Serializable
data class CoordinatesDto(
    @SerialName("lat") val lat: Double = 0.0,
    @SerialName("lng") val lng: Double = 0.0
)

@Serializable
data class AttractionDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("category") val category: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("crowd_density") val crowdDensity: String = "Medium",
    @SerialName("visit_duration_hrs") val visitDurationHrs: Float = 1.5f,
    @SerialName("best_time") val bestTime: String = "",
    @SerialName("image_url") val imageUrl: String = ""
)

@Serializable
data class DestinationAttributesDto(
    @SerialName("nature") val nature: Float = 5.0f,
    @SerialName("climate") val climate: String = "",
    @SerialName("activities") val activities: List<String> = emptyList(),
    @SerialName("culture") val culture: String = "",
    @SerialName("budget_level") val budgetLevel: String = "Moderate",
    @SerialName("avg_cost_per_day_inr") val avgCostPerDayInr: Int = 3000,
    @SerialName("accessibility") val accessibility: String = "",
    @SerialName("altitude_ft") val altitudeFt: Int = 0
)

@Serializable
data class DestinationDetailDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("tagline") val tagline: String = "",
    @SerialName("region") val region: String = "",
    @SerialName("state") val state: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("coordinates") val coordinates: CoordinatesDto? = null,
    @SerialName("hero_image") val heroImage: String = "",
    @SerialName("gallery_images") val galleryImages: List<String> = emptyList(),
    @SerialName("attributes") val attributes: DestinationAttributesDto? = null,
    @SerialName("highlights") val highlights: List<String> = emptyList(),
    @SerialName("attractions") val attractions: List<AttractionDto> = emptyList(),
    @SerialName("base_crowd_score") val baseCrowdScore: Int = 50,
    @SerialName("recommended_duration_days") val recommendedDurationDays: Int = 2,
    @SerialName("tags") val tags: List<String> = emptyList()
)

@Serializable
data class AlternativeRecommendationDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("tagline") val tagline: String = "",
    @SerialName("hero_image") val heroImage: String = "",
    @SerialName("crowd_score") val crowdScore: Int = 0,
    @SerialName("crowd_level") val crowdLevel: String = "LOW",
    @SerialName("similarity_score") val similarityScore: Int = 0,
    @SerialName("crowd_reduction_percent") val crowdReductionPercent: Int = 0,
    @SerialName("distance_km") val distanceKm: Float = 0f,
    @SerialName("estimated_cost_per_day") val estimatedCostPerDay: Int = 0,
    @SerialName("cost_difference_percent") val costDifferencePercent: Int = 0,
    @SerialName("reasons_to_recommend") val reasonsToRecommend: List<String> = emptyList(),
    @SerialName("shared_highlights") val sharedHighlights: List<String> = emptyList(),
    @SerialName("capacity_status") val capacityStatus: String = "HEALTHY",
    @SerialName("access_status") val accessStatus: String = "OPEN"
)

@Serializable
data class AlternativesResponseDto(
    @SerialName("origin_destination_id") val originDestinationId: String = "",
    @SerialName("alternatives") val alternatives: List<AlternativeRecommendationDto> = emptyList()
)
