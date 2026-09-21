package com.yatrisetu.app.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Destinations : Screen("destinations")
    object DestinationDetail : Screen("destination/{id}") {
        fun createRoute(id: String) = "destination/$id"
    }
    object Crowd : Screen("destination/{id}/crowd") {
        fun createRoute(id: String) = "destination/$id/crowd"
    }
    object Alternatives : Screen("destination/{id}/alternatives") {
        fun createRoute(id: String = "darjeeling") = "destination/$id/alternatives"
    }
    object Itinerary : Screen("itinerary")
    object Homestays : Screen("homestays")
    object Booking : Screen("booking?homestayId={homestayId}") {
        fun createRoute(homestayId: String) = "booking?homestayId=$homestayId"
    }
    object BookingConfirmation : Screen("booking/confirmation/{bookingId}") {
        fun createRoute(bookingId: String) = "booking/confirmation/$bookingId"
    }
    object Trip : Screen("trip/{id}") {
        fun createRoute(id: String = "YS-BK-7492A") = "trip/$id"
    }
    object Sos : Screen("safety/sos")
    object Dashboard : Screen("dashboard")
}
