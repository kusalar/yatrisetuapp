package com.yatrisetu.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.yatrisetu.app.presentation.theme.YatriColors
import com.yatrisetu.app.presentation.theme.YatriTypographyTokens

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Explore : BottomNavItem(
        route = Screen.Home.route,
        title = "Explore",
        icon = Icons.Default.Explore
    )

    object FlowAdvisor : BottomNavItem(
        route = Screen.Alternatives.createRoute("darjeeling"),
        title = "Flow Advisor",
        icon = Icons.Default.AltRoute
    )

    object MyTrip : BottomNavItem(
        route = Screen.Trip.createRoute("YS-BK-7492A"),
        title = "My Trip",
        icon = Icons.Default.Luggage
    )

    object RuralStays : BottomNavItem(
        route = Screen.Homestays.route,
        title = "Rural Stays",
        icon = Icons.Default.Home
    )
}

@Composable
fun YatriBottomBar(
    currentRoute: String?,
    onNavigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Explore,
        BottomNavItem.FlowAdvisor,
        BottomNavItem.MyTrip,
        BottomNavItem.RuralStays
    )

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route ||
                (item == BottomNavItem.Explore && currentRoute == Screen.Home.route) ||
                (item == BottomNavItem.FlowAdvisor && currentRoute?.contains("alternatives") == true) ||
                (item == BottomNavItem.MyTrip && currentRoute?.contains("trip") == true) ||
                (item == BottomNavItem.RuralStays && currentRoute == Screen.Homestays.route)

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateTo(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = YatriTypographyTokens.LabelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = YatriColors.BrandAmberPrimary,
                    selectedTextColor = YatriColors.BrandAmberPrimary,
                    indicatorColor = YatriColors.BrandAmberLight.copy(alpha = 0.2f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
