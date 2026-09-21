package com.yatrisetu.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatrisetu.app.data.repository.RepositoryProvider
import com.yatrisetu.app.domain.model.CrowdLevel
import com.yatrisetu.app.domain.model.DestinationSummary
import com.yatrisetu.app.presentation.common.CrowdScoreBadge
import com.yatrisetu.app.presentation.common.DestinationCard
import com.yatrisetu.app.presentation.common.StatusPillType
import com.yatrisetu.app.presentation.common.YatriButton
import com.yatrisetu.app.presentation.common.YatriButtonVariant
import com.yatrisetu.app.presentation.common.YatriCard
import com.yatrisetu.app.presentation.common.YatriStatusPill
import com.yatrisetu.app.presentation.theme.YatriColors
import com.yatrisetu.app.presentation.theme.YatriRadius
import com.yatrisetu.app.presentation.theme.YatriSpacing
import com.yatrisetu.app.presentation.theme.YatriTypographyTokens

@Composable
fun HomeScreen(
    onNavigateToDestination: (String) -> Unit,
    onNavigateToCrowd: (String) -> Unit,
    onNavigateToAlternatives: (String) -> Unit,
    onNavigateToItinerary: () -> Unit,
    onNavigateToHomestays: () -> Unit,
    onNavigateToSos: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(
            RepositoryProvider.getDestinationRepository(LocalContext.current)
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Header Branding & Refresh
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = YatriSpacing.l, vertical = YatriSpacing.m)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Yatri",
                                    style = YatriTypographyTokens.HeadlineLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Setu",
                                    style = YatriTypographyTokens.EditorialTitle,
                                    color = YatriColors.BrandAmber
                                )
                            }
                            Text(
                                text = "Himalayan Flow Intelligence",
                                style = YatriTypographyTokens.BodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { viewModel.loadDestinations(forceRefresh = true) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Destinations",
                                tint = YatriColors.BrandAmber
                            )
                        }
                    }
                }
            }

            // Offline Freshness Notice
            if (uiState.isOffline && uiState.freshnessNotice != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = YatriSpacing.l, vertical = YatriSpacing.xs)
                            .clip(RoundedCornerShape(YatriRadius.Card))
                            .background(YatriColors.BrandAmber.copy(alpha = 0.12f))
                            .padding(horizontal = YatriSpacing.m, vertical = YatriSpacing.s)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = "Offline Cache",
                                tint = YatriColors.BrandAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(YatriSpacing.s))
                            Text(
                                text = uiState.freshnessNotice ?: "Offline mode",
                                style = YatriTypographyTokens.BodyMedium.copy(fontSize = 12.sp),
                                color = YatriColors.BrandAmber
                            )
                        }
                    }
                }
            }

            // Search Bar
            item {
                Box(modifier = Modifier.padding(horizontal = YatriSpacing.l, vertical = YatriSpacing.xs)) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = {
                            Text(
                                "Search hill stations, tea gardens, passes...",
                                style = YatriTypographyTokens.BodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (uiState.selectedCrowdFilter != null) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Active filter",
                                    tint = YatriColors.BrandAmber
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(YatriRadius.Card),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = YatriColors.BrandAmber,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Crowd Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = YatriSpacing.l, vertical = YatriSpacing.xs),
                    horizontalArrangement = Arrangement.spacedBy(YatriSpacing.xs)
                ) {
                    CrowdLevel.values().forEach { level ->
                        val isSelected = uiState.selectedCrowdFilter == level
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onFilterSelected(level) },
                            label = {
                                Text(
                                    text = level.displayName,
                                    style = YatriTypographyTokens.LabelLarge.copy(fontSize = 12.sp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (level) {
                                    CrowdLevel.LOW -> YatriColors.CrowdLowBg
                                    CrowdLevel.MEDIUM -> YatriColors.CrowdMediumBg
                                    CrowdLevel.HIGH -> YatriColors.CrowdHighBg
                                    CrowdLevel.VERY_HIGH -> YatriColors.CrowdVeryHighBg
                                },
                                selectedLabelColor = when (level) {
                                    CrowdLevel.LOW -> YatriColors.CrowdLow
                                    CrowdLevel.MEDIUM -> YatriColors.CrowdMedium
                                    CrowdLevel.HIGH -> YatriColors.CrowdHigh
                                    CrowdLevel.VERY_HIGH -> YatriColors.CrowdVeryHigh
                                }
                            )
                        )
                    }
                }
            }

            // Regional Live Crowd Ticker (consuming real backend crowd data)
            if (uiState.destinations.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = YatriSpacing.m)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = YatriSpacing.l),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Regional Live Crowd",
                                style = YatriTypographyTokens.TitleLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            YatriStatusPill(
                                label = if (uiState.isOffline) "OFFLINE CACHE" else "CANONICAL LIVE",
                                type = if (uiState.isOffline) StatusPillType.CROWD_MODERATE else StatusPillType.CROWD_LOW
                            )
                        }
                        Spacer(modifier = Modifier.height(YatriSpacing.s))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = YatriSpacing.l),
                            horizontalArrangement = Arrangement.spacedBy(YatriSpacing.s)
                        ) {
                            items(uiState.destinations, key = { it.id }) { item ->
                                RegionalCrowdTickerItem(
                                    destination = item,
                                    onClick = { onNavigateToCrowd(item.id) }
                                )
                            }
                        }
                    }
                }
            }

            // Featured Destinations Section
            item {
                Column(modifier = Modifier.padding(top = YatriSpacing.l)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = YatriSpacing.l),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (uiState.searchQuery.isNotBlank() || uiState.selectedCrowdFilter != null) {
                                "Matching Destinations (${uiState.filteredDestinations.size})"
                            } else {
                                "Explore Destinations"
                            },
                            style = YatriTypographyTokens.HeadlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(YatriSpacing.s))
                }
            }

            // Destinations List or Empty/Loading State
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = YatriColors.BrandAmber)
                    }
                }
            } else if (uiState.errorMessage != null && uiState.destinations.isEmpty()) {
                item {
                    YatriCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = YatriSpacing.l)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(YatriSpacing.l),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = YatriColors.SosRedPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(YatriSpacing.s))
                            Text(
                                text = uiState.errorMessage ?: "Failed to load destinations",
                                style = YatriTypographyTokens.BodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(YatriSpacing.m))
                            YatriButton(
                                onClick = { viewModel.loadDestinations(forceRefresh = true) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Retry Connection")
                            }
                        }
                    }
                }
            } else if (uiState.filteredDestinations.isEmpty()) {
                item {
                    YatriCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = YatriSpacing.l)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(YatriSpacing.l),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No destinations match your filters.",
                                style = YatriTypographyTokens.HeadlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(YatriSpacing.s))
                            Text(
                                text = "Try clearing search keywords or crowd level filters.",
                                style = YatriTypographyTokens.BodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(YatriSpacing.m))
                            YatriButton(
                                onClick = {
                                    viewModel.onSearchQueryChanged("")
                                    viewModel.onFilterSelected(null)
                                },
                                variant = YatriButtonVariant.SECONDARY
                            ) {
                                Text("Clear Filters")
                            }
                        }
                    }
                }
            } else {
                items(uiState.filteredDestinations, key = { it.id }) { dest ->
                    Box(modifier = Modifier.padding(horizontal = YatriSpacing.l, vertical = YatriSpacing.xs)) {
                        DestinationCard(
                            destination = dest,
                            onClick = { onNavigateToDestination(dest.id) }
                        )
                    }
                }
            }

            // Quick Actions Section
            item {
                Column(modifier = Modifier.padding(top = YatriSpacing.l)) {
                    Text(
                        text = "Smart Travel Systems",
                        style = YatriTypographyTokens.HeadlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = YatriSpacing.l)
                    )
                    Spacer(modifier = Modifier.height(YatriSpacing.s))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = YatriSpacing.l),
                        horizontalArrangement = Arrangement.spacedBy(YatriSpacing.m)
                    ) {
                        ActionCard(
                            title = "Flow Advisor",
                            subtitle = "Calm alternatives",
                            icon = Icons.Default.Navigation,
                            onClick = { onNavigateToAlternatives("darjeeling") },
                            modifier = Modifier.weight(1f)
                        )
                        ActionCard(
                            title = "AI Itinerary",
                            subtitle = "Crowd-avoiding",
                            icon = Icons.Default.CalendarMonth,
                            onClick = onNavigateToItinerary,
                            modifier = Modifier.weight(1f)
                        )
                        ActionCard(
                            title = "Rural Stays",
                            subtitle = "Verified homes",
                            icon = Icons.Default.Home,
                            onClick = onNavigateToHomestays,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Safety / SOS Quick Access Card
            item {
                Spacer(modifier = Modifier.height(YatriSpacing.l))
                YatriCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = YatriSpacing.l)
                        .clickable { onNavigateToSos() },
                    backgroundColor = YatriColors.SosRedPrimary.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        YatriColors.SosRedPrimary.copy(alpha = 0.25f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(YatriSpacing.m),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(YatriColors.SosRedPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Emergency SOS",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(YatriSpacing.m))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Emergency Safety & Helplines",
                                style = YatriTypographyTokens.TitleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "24/7 mountain safety desk & verified tourist helplines",
                                style = YatriTypographyTokens.BodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        YatriStatusPill(label = "SOS", type = StatusPillType.CROWD_VERY_HIGH)
                    }
                }
            }
        }

        // Top subtle progress bar during pull/background refresh
        if (uiState.isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                color = YatriColors.BrandAmber
            )
        }
    }
}

@Composable
private fun RegionalCrowdTickerItem(
    destination: DestinationSummary,
    onClick: () -> Unit
) {
    YatriCard(
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(170.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(YatriSpacing.s)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = destination.name,
                    style = YatriTypographyTokens.TitleLarge.copy(fontSize = 15.sp),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = destination.freshnessLabel,
                    style = YatriTypographyTokens.LabelLarge.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (destination.isLive) YatriColors.CrowdLow else YatriColors.BrandAmber
                )
            }
            Spacer(modifier = Modifier.height(YatriSpacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CrowdScoreBadge(
                    score = destination.crowdScore,
                    level = destination.crowdLevel
                )
                Text(
                    text = "₹${destination.avgCostPerDayInr}/d",
                    style = YatriTypographyTokens.BodyMedium.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    YatriCard(
        modifier = modifier.clickable(onClick = onClick),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(YatriSpacing.m),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(YatriRadius.Button))
                    .background(YatriColors.BrandAmber.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = YatriColors.BrandAmber,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(YatriSpacing.s))
            Text(
                text = title,
                style = YatriTypographyTokens.LabelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = YatriTypographyTokens.BodyMedium.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
