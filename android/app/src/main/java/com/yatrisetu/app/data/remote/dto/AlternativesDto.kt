package com.yatrisetu.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlternativeWeatherDto(
    @SerialName("destination_id") val destinationId: String = "",
    @SerialName("temperature") val temperature: Float = 0f,
    @SerialName("temp_min_c") val tempMinC: Float? = null,
    @SerialName("temp_max_c") val tempMaxC: Float? = null,
    @SerialName("condition") val condition: String = "",
    @SerialName("humidity") val humidity: Int? = null,
    @SerialName("precipitation_chance") val precipitationChance: Int? = null,
    @SerialName("provenance_label") val provenanceLabel: String = "DEMO MODE",
    @SerialName("provider_mode") val providerMode: String = "DEMO",
    @SerialName("cache_status") val cacheStatus: String = "LIVE",
    @SerialName("observed_at") val observedAt: String? = null,
    @SerialName("temperature_range") val temperatureRange: String? = null
)

@Serializable
data class AlternativeRecommendationDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("tagline") val tagline: String = "",
    @SerialName("state") val state: String = "",
    @SerialName("hero_image") val heroImage: String = "",
    @SerialName("crowd_score") val crowdScore: Int = 0,
    @SerialName("crowd_level") val crowdLevel: String = "LOW",
    @SerialName("similarity_score") val similarityScore: Int = 0,
    @SerialName("similarity_provenance") val similarityProvenance: String? = null,
    @SerialName("original_crowd_score") val originalCrowdScore: Int = 0,
    @SerialName("alternative_crowd_score") val alternativeCrowdScore: Int = 0,
    @SerialName("crowd_reduction_percent") val crowdReductionPercent: Int = 0,
    @SerialName("distance_km") val distanceKm: Float = 0f,
    @SerialName("geographic_distance_km") val geographicDistanceKm: Float? = null,
    @SerialName("road_distance_km") val roadDistanceKm: Float? = null,
    @SerialName("distance_provenance") val distanceProvenance: String = "HAVERSINE_GEOGRAPHIC_ESTIMATE",
    @SerialName("estimated_cost_per_day") val estimatedCostPerDay: Int = 0,
    @SerialName("cost_difference_percent") val costDifferencePercent: Int = 0,
    @SerialName("reasons_to_recommend") val reasonsToRecommend: List<String> = emptyList(),
    @SerialName("shared_highlights") val sharedHighlights: List<String> = emptyList(),
    @SerialName("matching_attributes") val matchingAttributes: List<String> = emptyList(),
    @SerialName("key_experience") val keyExperience: String = "",
    @SerialName("eco_tag") val ecoTag: String = "",
    @SerialName("destination_id") val destinationId: String? = null,
    @SerialName("current_pressure") val currentPressure: Int? = null,
    @SerialName("expected_pressure") val expectedPressure: Int? = null,
    @SerialName("capacity_status") val capacityStatus: String = "HEALTHY",
    @SerialName("available_capacity") val availableCapacity: Int? = null,
    @SerialName("access_status") val accessStatus: String = "OPEN",
    @SerialName("weather") val weather: AlternativeWeatherDto? = null,
    @SerialName("weather_summary") val weatherSummary: String? = null,
    @SerialName("traffic_summary") val trafficSummary: String? = null,
    @SerialName("homestay_availability") val homestayAvailability: String? = null,
    @SerialName("reasons") val reasons: List<String> = emptyList(),
    @SerialName("provenance") val provenance: String = "REAL — YATRI SETU NETWORK",
    @SerialName("last_updated") val lastUpdated: String? = null
)

@Serializable
data class AlternativesResponseDto(
    @SerialName("origin_destination_id") val originDestinationId: String = "",
    @SerialName("origin_destination_name") val originDestinationName: String = "",
    @SerialName("origin_crowd_score") val originCrowdScore: Int = 0,
    @SerialName("origin_crowd_level") val originCrowdLevel: String = "HIGH",
    @SerialName("alternatives") val alternatives: List<AlternativeRecommendationDto> = emptyList()
)

@Serializable
data class AlternativeAcceptanceRequestDto(
    @SerialName("alternative_destination_id") val alternativeDestinationId: String,
    @SerialName("original_destination_id") val originalDestinationId: String? = null,
    @SerialName("origin_destination_id") val originDestinationId: String? = null,
    @SerialName("similarity_score") val similarityScore: Int? = null,
    @SerialName("session_id") val sessionId: String? = null
)

@Serializable
data class AlternativeAcceptanceResponseDto(
    @SerialName("status") val status: String = "recorded",
    @SerialName("event_id") val eventId: String = "",
    @SerialName("original_destination_id") val originalDestinationId: String = "",
    @SerialName("alternative_destination_id") val alternativeDestinationId: String = ""
)
