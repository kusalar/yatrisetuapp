package com.yatrisetu.app.domain.model

data class ActivitySlot(
    val timeSlot: String, // "Morning", "Afternoon", "Evening"
    val title: String,
    val description: String,
    val crowdAvoidanceTip: String,
    val estimatedCostInr: Int
)

data class ItineraryDay(
    val dayNumber: Int,
    val theme: String,
    val slots: List<ActivitySlot>
)

data class Itinerary(
    val destinationId: String,
    val destinationName: String,
    val crowdAvoidanceScore: String, // e.g. "91% Overcrowding Avoided"
    val days: List<ItineraryDay>,
    val localImpactStatement: String
)
