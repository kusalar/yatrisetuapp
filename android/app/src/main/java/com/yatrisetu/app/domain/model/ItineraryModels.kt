package com.yatrisetu.app.domain.model

data class ActivitySlot(
    val timeSlot: String,
    val period: String = "Morning",
    val title: String,
    val description: String,
    val locationName: String = "",
    val crowdForecast: String = "Low",
    val costEstimateInr: Int = 0,
    val durationHrs: Float = 1.5f,
    val travelTip: String? = null,
    val category: String = "Nature",
    val imageUrl: String? = null,
    val isWeatherAdapted: Boolean = false,
    val adaptationReason: String? = null
) {
    val formattedCost: String
        get() = if (costEstimateInr > 0) "₹$costEstimateInr" else "Free"

    val durationLabel: String
        get() = if (durationHrs == durationHrs.toInt().toFloat()) {
            "${durationHrs.toInt()}h"
        } else {
            "${durationHrs}h"
        }
}

data class ItineraryDay(
    val dayNumber: Int,
    val theme: String,
    val overview: String = "",
    val estimatedBudgetInr: Int = 0,
    val activities: List<ActivitySlot> = emptyList(),
    val transitAdvice: String = ""
) {
    val formattedBudget: String
        get() = "₹$estimatedBudgetInr"
}

data class Itinerary(
    val itineraryId: String = "",
    val destinationId: String,
    val destinationName: String,
    val durationDays: Int,
    val travelerType: String = "Solo",
    val pace: String = "Moderate",
    val interests: List<String> = emptyList(),
    val totalEstimatedBudgetInr: Int = 0,
    val crowdAvoidanceRating: String = "",
    val localEconomicImpactTag: String = "",
    val days: List<ItineraryDay> = emptyList(),
    val aiGeneratedNote: String = "",
    val sustainabilityScore: Int = 88,
    val sustainabilityClassification: String = "EXCELLENT",
    val weatherAdaptationNotice: String? = null,
    val whyThisItinerary: List<String> = emptyList(),
    val aiProviderUsed: String = "mock",
    val optimizationHistory: List<String> = emptyList(),
    val isLive: Boolean = true,
    val cachedAt: Long? = null
) {
    val formattedTotalBudget: String
        get() = "₹$totalEstimatedBudgetInr"

    val tripOverviewLabel: String
        get() = "$durationDays Days · $travelerType · $pace Pace"

    val freshnessLabel: String
        get() = if (isLive) {
            "LIVE · AI Generated"
        } else {
            val minutesAgo = cachedAt?.let { (System.currentTimeMillis() - it) / (1000 * 60) } ?: 0
            if (minutesAgo <= 1) "Offline · Saved just now" else "Offline · Saved ${minutesAgo}m ago"
        }
}

data class ItineraryRequest(
    val destinationId: String,
    val durationDays: Int = 3,
    val travelerType: String = "Solo",
    val pace: String = "Moderate",
    val interests: List<String> = listOf("Nature", "Culture", "Local Food"),
    val budgetLevel: String = "Moderate",
    val startDate: String? = null,
    val optimizeForWeather: Boolean = true
)
