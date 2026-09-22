package com.yatrisetu.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItineraryRequestDto(
    @SerialName("destination_id") val destinationId: String,
    @SerialName("duration_days") val durationDays: Int = 3,
    @SerialName("traveler_type") val travelerType: String = "Solo",
    @SerialName("pace") val pace: String = "Moderate",
    @SerialName("interests") val interests: List<String> = listOf("Nature", "Culture", "Local Food"),
    @SerialName("budget_level") val budgetLevel: String = "Moderate",
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("optimize_for_weather") val optimizeForWeather: Boolean = true
)

@Serializable
data class ActivitySlotDto(
    @SerialName("time_slot") val timeSlot: String = "",
    @SerialName("period") val period: String = "Morning",
    @SerialName("title") val title: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("location_name") val locationName: String = "",
    @SerialName("crowd_forecast") val crowdForecast: String = "Low",
    @SerialName("cost_estimate_inr") val costEstimateInr: Int = 0,
    @SerialName("duration_hrs") val durationHrs: Float = 1.5f,
    @SerialName("travel_tip") val travelTip: String? = null,
    @SerialName("category") val category: String = "Nature",
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("is_weather_adapted") val isWeatherAdapted: Boolean = false,
    @SerialName("adaptation_reason") val adaptationReason: String? = null
)

@Serializable
data class ItineraryDayDto(
    @SerialName("day_number") val dayNumber: Int,
    @SerialName("theme") val theme: String = "",
    @SerialName("overview") val overview: String = "",
    @SerialName("estimated_budget_inr") val estimatedBudgetInr: Int = 0,
    @SerialName("activities") val activities: List<ActivitySlotDto> = emptyList(),
    @SerialName("transit_advice") val transitAdvice: String = ""
)

@Serializable
data class ItineraryResponseDto(
    @SerialName("itinerary_id") val itineraryId: String = "",
    @SerialName("destination_id") val destinationId: String = "",
    @SerialName("destination_name") val destinationName: String = "",
    @SerialName("duration_days") val durationDays: Int = 3,
    @SerialName("traveler_type") val travelerType: String = "Solo",
    @SerialName("pace") val pace: String = "Moderate",
    @SerialName("interests") val interests: List<String> = emptyList(),
    @SerialName("total_estimated_budget_inr") val totalEstimatedBudgetInr: Int = 0,
    @SerialName("crowd_avoidance_rating") val crowdAvoidanceRating: String = "",
    @SerialName("local_economic_impact_tag") val localEconomicImpactTag: String = "",
    @SerialName("days") val days: List<ItineraryDayDto> = emptyList(),
    @SerialName("ai_generated_note") val aiGeneratedNote: String = "",
    @SerialName("sustainability_score") val sustainabilityScore: Int = 88,
    @SerialName("sustainability_classification") val sustainabilityClassification: String = "EXCELLENT",
    @SerialName("weather_adaptation_notice") val weatherAdaptationNotice: String? = null,
    @SerialName("why_this_itinerary") val whyThisItinerary: List<String> = emptyList(),
    @SerialName("ai_provider_used") val aiProviderUsed: String = "mock",
    @SerialName("optimization_history") val optimizationHistory: List<String> = emptyList()
)

@Serializable
data class ItineraryOptimizeRequestDto(
    @SerialName("itinerary_id") val itineraryId: String,
    @SerialName("destination_id") val destinationId: String,
    @SerialName("instruction") val instruction: String,
    @SerialName("custom_instruction") val customInstruction: String? = null,
    @SerialName("current_itinerary") val currentItinerary: ItineraryResponseDto? = null
)
