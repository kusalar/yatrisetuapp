package com.yatrisetu.app.domain.model

data class DigitalPass(
    val passCode: String,
    val bookingId: String,
    val travelerName: String,
    val destinationName: String,
    val homestayTitle: String,
    val dates: String,
    val travelersCount: Int,
    val qrPayload: String,
    val checkpostVerified: Boolean
)

data class TripDetails(
    val tripId: String,
    val destinationId: String,
    val destinationName: String,
    val homestayName: String,
    val dates: String,
    val status: String,
    val travelersCount: Int,
    val digitalPassCode: String,
    val weatherAlert: String,
    val hostSupportNumber: String,
    val checkInLocation: String,
    val packingChecklist: List<String>
)
