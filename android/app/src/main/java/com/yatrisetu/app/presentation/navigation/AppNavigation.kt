package com.yatrisetu.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yatrisetu.app.presentation.alternatives.AlternativesScreen
import com.yatrisetu.app.presentation.common.YatriTopBar
import com.yatrisetu.app.presentation.crowd.CrowdIntelligenceScreen
import com.yatrisetu.app.presentation.destination.DestinationDetailScreen
import com.yatrisetu.app.presentation.home.HomeScreen
import com.yatrisetu.app.presentation.homestays.HomestaysScreen
import com.yatrisetu.app.presentation.itinerary.ItineraryScreen
import com.yatrisetu.app.presentation.sos.SosScreen
import com.yatrisetu.app.presentation.trip.ActiveTripScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isTopLevelRoute = currentRoute == Screen.Home.route ||
        currentRoute?.contains("alternatives") == true ||
        currentRoute?.contains("trip") == true ||
        currentRoute == Screen.Homestays.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Homestays.route
    ) || currentRoute?.contains("alternatives") == true || currentRoute?.contains("trip") == true

    Scaffold(
        topBar = {
            YatriTopBar(
                title = when {
                    currentRoute?.contains("crowd") == true -> "Crowd Intelligence"
                    currentRoute?.contains("alternatives") == true -> "Flow Advisor"
                    currentRoute?.contains("itinerary") == true -> "AI Itinerary"
                    currentRoute?.contains("homestays") == true -> "Rural Homestays"
                    currentRoute?.contains("trip") == true -> "My Trip"
                    currentRoute?.contains("sos") == true -> "Emergency SOS"
                    else -> null
                },
                canNavigateBack = !isTopLevelRoute,
                onNavigateBack = { navController.popBackStack() },
                showSosAction = currentRoute != Screen.Sos.route,
                onSosClick = { navController.navigate(Screen.Sos.route) }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                YatriBottomBar(
                    currentRoute = currentRoute,
                    onNavigateTo = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToDestination = { id ->
                        navController.navigate(Screen.DestinationDetail.createRoute(id))
                    },
                    onNavigateToCrowd = { id ->
                        navController.navigate(Screen.Crowd.createRoute(id))
                    },
                    onNavigateToAlternatives = { id ->
                        navController.navigate(Screen.Alternatives.createRoute(id))
                    },
                    onNavigateToItinerary = {
                        navController.navigate(Screen.Itinerary.createRoute())
                    },
                    onNavigateToHomestays = {
                        navController.navigate(Screen.Homestays.route)
                    },
                    onNavigateToSos = {
                        navController.navigate(Screen.Sos.route)
                    }
                )
            }

            composable(
                route = Screen.DestinationDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val destinationId = backStackEntry.arguments?.getString("id") ?: "darjeeling"
                DestinationDetailScreen(
                    destinationId = destinationId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCrowd = { id ->
                        navController.navigate(Screen.Crowd.createRoute(id))
                    },
                    onNavigateToAlternatives = { id ->
                        navController.navigate(Screen.Alternatives.createRoute(id))
                    },
                    onNavigateToItinerary = { id, name ->
                        navController.navigate(Screen.Itinerary.createRoute(id, name))
                    }
                )
            }

            composable(
                route = Screen.Crowd.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val destinationId = backStackEntry.arguments?.getString("id") ?: "darjeeling"
                CrowdIntelligenceScreen(
                    destinationId = destinationId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAlternatives = { id ->
                        navController.navigate(Screen.Alternatives.createRoute(id))
                    }
                )
            }

            composable(
                route = Screen.Alternatives.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType; defaultValue = "darjeeling" })
            ) { backStackEntry ->
                val destinationId = backStackEntry.arguments?.getString("id") ?: "darjeeling"
                AlternativesScreen(
                    originId = destinationId,
                    onNavigateBack = { navController.popBackStack() },
                    onSelectAlternative = { altId ->
                        navController.navigate(Screen.DestinationDetail.createRoute(altId))
                    }
                )
            }

            composable(
                route = Screen.Itinerary.route,
                arguments = listOf(
                    navArgument("destinationId") {
                        type = NavType.StringType
                        defaultValue = "darjeeling"
                    },
                    navArgument("destinationName") {
                        type = NavType.StringType
                        defaultValue = "Darjeeling"
                    }
                )
            ) { backStackEntry ->
                val rawId = backStackEntry.arguments?.getString("destinationId") ?: "darjeeling"
                val destinationId = if (rawId.startsWith("{") || rawId.isBlank()) "darjeeling" else rawId
                val rawName = backStackEntry.arguments?.getString("destinationName") ?: "Darjeeling"
                val destinationName = if (rawName.startsWith("{") || rawName.isBlank()) "Darjeeling" else rawName
                ItineraryScreen(
                    destinationId = destinationId,
                    destinationName = destinationName,
                    onNavigateBack = { navController.popBackStack() },
                    onSelectHomestay = {
                        navController.navigate(Screen.Homestays.route)
                    }
                )
            }

            composable(Screen.Homestays.route) {
                HomestaysScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onReserveHomestay = { homestayId ->
                        navController.navigate(Screen.Trip.createRoute("YS-BK-7492A"))
                    }
                )
            }

            composable(
                route = Screen.Trip.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType; defaultValue = "YS-BK-7492A" })
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("id") ?: "YS-BK-7492A"
                ActiveTripScreen(
                    tripId = tripId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Sos.route) {
                SosScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
