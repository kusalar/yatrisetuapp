package com.yatrisetu.app.data.repository

interface BookingRepository {
    suspend fun getActiveTrip(tripId: String)
}

interface SafetyRepository {
    suspend fun triggerSosAlert(latitude: Double?, longitude: Double?, notes: String?)
    suspend fun cancelSosAlert(incidentId: String, reason: String)
}
