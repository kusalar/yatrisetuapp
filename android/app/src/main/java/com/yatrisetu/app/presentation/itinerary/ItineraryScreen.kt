package com.yatrisetu.app.presentation.itinerary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatrisetu.app.data.repository.RepositoryProvider
import com.yatrisetu.app.domain.model.ActivitySlot
import com.yatrisetu.app.domain.model.Itinerary
import com.yatrisetu.app.domain.model.ItineraryDay
import com.yatrisetu.app.presentation.common.StatusPillType
import com.yatrisetu.app.presentation.common.YatriButton
import com.yatrisetu.app.presentation.common.YatriButtonVariant
import com.yatrisetu.app.presentation.common.YatriCard
import com.yatrisetu.app.presentation.common.YatriStatusPill
import com.yatrisetu.app.presentation.theme.YatriColors
import com.yatrisetu.app.presentation.theme.YatriRadius
import com.yatrisetu.app.presentation.theme.YatriSpacing
import com.yatrisetu.app.presentation.theme.YatriTypographyTokens

private val AvailableInterests = listOf(
    "Nature", "Culture", "Food", "Adventure", "Photography", "Heritage", "Wellness"
)
private val TravelerTypes = listOf("SOLO", "COUPLE", "FAMILY", "FRIENDS")
private val PaceOptions = listOf("RELAXED", "BALANCED", "PACKED")
private val BudgetLevels = listOf("BUDGET", "MODERATE", "LUXURY")

private val OptimizationDirectives = listOf(
    "AVOID_CROWDS" to "Avoid Crowds",
    "RAIN_SAFE" to "Rain Safe",
    "MORE_NATURE" to "More Nature",
    "MORE_CULTURE" to "More Culture",
    "MAKE_CHEAPER" to "Lower Cost",
    "MORE_RELAXED" to "More Relaxed"
)

@Composable
fun ItineraryScreen(
    destinationId: String = "darjeeling",
    destinationName: String = "Darjeeling",
    onNavigateBack: () -> Unit,
    onSelectHomestay: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItineraryViewModel = viewModel(
        factory = ItineraryViewModel.Factory(
            itineraryRepository = RepositoryProvider.getItineraryRepository(LocalContext.current),
            destinationId = destinationId,
            destinationName = destinationName
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    // Sync route arguments with ViewModel if navigated with new destination
    LaunchedEffect(destinationId, destinationName) {
        if (uiState.destinationId != destinationId.lowercase().trim()) {
            viewModel.setDestination(destinationId, destinationName)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Navigation Bar
            ItineraryTopBar(
                title = if (uiState.mode == ItineraryUiMode.FORM) "Plan Your Journey" else "${uiState.destinationName} Itinerary",
                subtitle = if (uiState.mode == ItineraryUiMode.FORM) "Sustainable AI Himalayan Planner" else "AI Crowd-Aware Plan",
                onNavigateBack = onNavigateBack,
                onEditPreferences = if (uiState.mode == ItineraryUiMode.RESULT) {
                    { viewModel.editPreferences() }
                } else null
            )

            // Content Area based on UI Mode
            when (uiState.mode) {
                ItineraryUiMode.FORM -> {
                    ItineraryFormContent(
                        uiState = uiState,
                        onDurationChange = { viewModel.setDuration(it) },
                        onTravelerTypeChange = { viewModel.setTravelerType(it) },
                        onPaceChange = { viewModel.setPace(it) },
                        onToggleInterest = { viewModel.toggleInterest(it) },
                        onBudgetChange = { viewModel.setBudgetLevel(it) },
                        onWeatherToggle = { viewModel.setOptimizeForWeather(it) },
                        onGenerateClick = { viewModel.generateItinerary() }
                    )
                }
                ItineraryUiMode.GENERATING, ItineraryUiMode.OPTIMIZING -> {
                    ItineraryLoadingContent(
                        progressMessage = uiState.progressMessage,
                        isOptimizing = uiState.mode == ItineraryUiMode.OPTIMIZING
                    )
                }
                ItineraryUiMode.RESULT -> {
                    uiState.currentItinerary?.let { itinerary ->
                        ItineraryResultContent(
                            itinerary = itinerary,
                            previousItinerary = uiState.previousItinerary,
                            selectedDayIndex = uiState.selectedDayIndex,
                            isFromCache = uiState.isFromCache,
                            onSelectDay = { viewModel.selectDay(it) },
                            onOptimize = { directive -> viewModel.optimizeItinerary(directive) }
                        )
                    } ?: run {
                        ItineraryErrorContent(
                            message = uiState.errorMessage ?: "No itinerary available.",
                            onRetry = { viewModel.generateItinerary() },
                            onEditPreferences = { viewModel.editPreferences() }
                        )
                    }
                }
                ItineraryUiMode.ERROR -> {
                    ItineraryErrorContent(
                        message = uiState.errorMessage ?: "We couldn't reach Yatri Setu to plan your itinerary.",
                        onRetry = { viewModel.retry() },
                        onEditPreferences = { viewModel.editPreferences() }
                    )
                }
            }
        }
    }
}

// =====================================================================
// Top Bar
// =====================================================================

@Composable
private fun ItineraryTopBar(
    title: String,
    subtitle: String,
    onNavigateBack: () -> Unit,
    onEditPreferences: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = YatriSpacing.s, vertical = YatriSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.semantics { contentDescription = "Navigate back" }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = YatriTypographyTokens.TitleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = YatriTypographyTokens.BodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (onEditPreferences != null) {
            IconButton(
                onClick = onEditPreferences,
                modifier = Modifier.semantics { contentDescription = "Edit planning preferences" }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = YatriColors.BrandAmber
                )
            }
        }
    }
}

// =====================================================================
// 1. Form Content
// =====================================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ItineraryFormContent(
    uiState: ItineraryUiState,
    onDurationChange: (Int) -> Unit,
    onTravelerTypeChange: (String) -> Unit,
    onPaceChange: (String) -> Unit,
    onToggleInterest: (String) -> Unit,
    onBudgetChange: (String) -> Unit,
    onWeatherToggle: (Boolean) -> Unit,
    onGenerateClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = YatriSpacing.l),
        verticalArrangement = Arrangement.spacedBy(YatriSpacing.l)
    ) {
        item {
            Spacer(modifier = Modifier.height(YatriSpacing.xs))
            // Destination Banner Card
            YatriCard(modifier = Modifier.fillMaxWidth()) {
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
                            .background(YatriColors.BrandAmber.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = YatriColors.BrandAmber,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(YatriSpacing.m))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Destination Selected",
                            style = YatriTypographyTokens.LabelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = uiState.destinationName,
                            style = YatriTypographyTokens.HeadlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    YatriStatusPill(
                        label = "AI READY",
                        type = StatusPillType.CROWD_LOW
                    )
                }
            }
        }

        // Duration Stepper (1 to 5 days)
        item {
            Column {
                Text(
                    text = "Trip Duration",
                    style = YatriTypographyTokens.TitleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Plan 1 to 5 days of crowd-balanced experiences",
                    style = YatriTypographyTokens.BodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(YatriSpacing.s))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.durationDays} ${if (uiState.durationDays == 1) "Day" else "Days"}",
                        style = YatriTypographyTokens.HeadlineLarge.copy(color = YatriColors.BrandAmber, fontWeight = FontWeight.Bold)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(YatriSpacing.s),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onDurationChange(uiState.durationDays - 1) },
                            enabled = uiState.durationDays > 1,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(YatriRadius.Button))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .semantics { contentDescription = "Decrease duration" }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = null,
                                tint = if (uiState.durationDays > 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }

                        IconButton(
                            onClick = { onDurationChange(uiState.durationDays + 1) },
                            enabled = uiState.durationDays < 5,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(YatriRadius.Button))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .semantics { contentDescription = "Increase duration" }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = if (uiState.durationDays < 5) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }

        // Traveler Type Chips
        item {
            Column {
                Text(
                    text = "Who is Travelling?",
                    style = YatriTypographyTokens.TitleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(YatriSpacing.s))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(YatriSpacing.s),
                    verticalArrangement = Arrangement.spacedBy(YatriSpacing.xs)
                ) {
                    TravelerTypes.forEach { type ->
                        val selected = uiState.travelerType == type
                        FilterChip(
                            selected = selected,
                            onClick = { onTravelerTypeChange(type) },
                            label = { Text(type.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = YatriColors.BrandAmber,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }
        }

        // Pace Chips
        item {
            Column {
                Text(
                    text = "Preferred Pace",
                    style = YatriTypographyTokens.TitleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(YatriSpacing.s))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(YatriSpacing.s),
                    verticalArrangement = Arrangement.spacedBy(YatriSpacing.xs)
                ) {
                    PaceOptions.forEach { pace ->
                        val selected = uiState.pace == pace
                        FilterChip(
                            selected = selected,
                            onClick = { onPaceChange(pace) },
                            label = { Text(pace.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = YatriColors.BrandAmber,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }
        }

        // Interests Multi-Select Chips
        item {
            Column {
                Text(
                    text = "Interests & Themes",
                    style = YatriTypographyTokens.TitleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Pick the activities you love most",
                    style = YatriTypographyTokens.BodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(YatriSpacing.s))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(YatriSpacing.s),
                    verticalArrangement = Arrangement.spacedBy(YatriSpacing.xs)
                ) {
                    AvailableInterests.forEach { interest ->
                        val selected = uiState.interests.contains(interest)
                        FilterChip(
                            selected = selected,
                            onClick = { onToggleInterest(interest) },
                            label = { Text(interest) },
                            leadingIcon = if (selected) {
                                { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = YatriColors.CrowdLow,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Budget Level Chips
        item {
            Column {
                Text(
                    text = "Budget Level",
                    style = YatriTypographyTokens.TitleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(YatriSpacing.s))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(YatriSpacing.s),
                    verticalArrangement = Arrangement.spacedBy(YatriSpacing.xs)
                ) {
                    BudgetLevels.forEach { budget ->
                        val selected = uiState.budgetLevel == budget
                        FilterChip(
                            selected = selected,
                            onClick = { onBudgetChange(budget) },
                            label = { Text(budget.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = YatriColors.BrandAmber,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }
        }

        // Weather Adaptation Switch
        item {
            YatriCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(YatriSpacing.m),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = null,
                                tint = YatriColors.BrandAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(YatriSpacing.xs))
                            Text(
                                text = "Himalayan Weather Sync",
                                style = YatriTypographyTokens.TitleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Adapt outdoor schedules for mountain rain & fog",
                            style = YatriTypographyTokens.BodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.optimizeForWeather,
                        onCheckedChange = onWeatherToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = YatriColors.BrandAmber,
                            checkedTrackColor = YatriColors.BrandAmber.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        // Submit Button
        item {
            Spacer(modifier = Modifier.height(YatriSpacing.m))
            YatriButton(
                onClick = onGenerateClick,
                enabled = uiState.canGenerate,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Generate My Itinerary" },
                variant = YatriButtonVariant.PRIMARY
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(YatriSpacing.s))
                Text("Generate My Itinerary")
            }
            Spacer(modifier = Modifier.height(YatriSpacing.xl))
        }
    }
}

// =====================================================================
// 2. Loading State Content
// =====================================================================

@Composable
private fun ItineraryLoadingContent(
    progressMessage: String,
    isOptimizing: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(YatriSpacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = YatriColors.BrandAmber,
            modifier = Modifier.size(56.dp),
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(YatriSpacing.l))
        Text(
            text = if (isOptimizing) "Optimizing Your Itinerary" else "Crafting Your Himalayan Plan",
            style = YatriTypographyTokens.HeadlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(YatriSpacing.s))
        Text(
            text = progressMessage,
            style = YatriTypographyTokens.BodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// =====================================================================
// 3. Result View Content
// =====================================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ItineraryResultContent(
    itinerary: Itinerary,
    previousItinerary: Itinerary?,
    selectedDayIndex: Int,
    isFromCache: Boolean,
    onSelectDay: (Int) -> Unit,
    onOptimize: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = YatriSpacing.l),
        verticalArrangement = Arrangement.spacedBy(YatriSpacing.m)
    ) {
        // Offline / Cache Banner if applicable
        if (isFromCache) {
            item {
                YatriCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(YatriSpacing.m),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = YatriColors.CrowdMedium,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(YatriSpacing.s))
                        Text(
                            text = "Saved Itinerary (Offline) • Connect to generate fresh live AI plan",
                            style = YatriTypographyTokens.BodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Summary Card
        item {
            ItinerarySummaryCard(itinerary = itinerary)
        }

        // Optimization Directive Quick Actions
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = YatriColors.BrandAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(YatriSpacing.xs))
                        Text(
                            text = "Optimize Itinerary",
                            style = YatriTypographyTokens.TitleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    if (itinerary.optimizationHistory.isNotEmpty()) {
                        Text(
                            text = "${itinerary.optimizationHistory.size} applied",
                            style = YatriTypographyTokens.LabelSmall,
                            color = YatriColors.CrowdLow
                        )
                    }
                }
                Spacer(modifier = Modifier.height(YatriSpacing.xs))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(YatriSpacing.s),
                    verticalArrangement = Arrangement.spacedBy(YatriSpacing.xs)
                ) {
                    OptimizationDirectives.forEach { (directive, label) ->
                        FilterChip(
                            selected = false,
                            onClick = { onOptimize(directive) },
                            label = { Text(label, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = YatriColors.BrandAmber
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }
        }

        // Weather Adaptation Alert Notice (if supplied by backend)
        if (!itinerary.weatherAdaptationNotice.isNullOrBlank()) {
            item {
                YatriCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(YatriSpacing.m),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = null,
                            tint = YatriColors.BrandAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(YatriSpacing.s))
                        Column {
                            Text(
                                text = "Weather Adaptation Notice",
                                style = YatriTypographyTokens.TitleMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = itinerary.weatherAdaptationNotice,
                                style = YatriTypographyTokens.BodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Day Tabs
        if (itinerary.days.isNotEmpty()) {
            val safeIndex = selectedDayIndex.coerceIn(0, itinerary.days.size - 1)
            item {
                ScrollableTabRow(
                    selectedTabIndex = safeIndex,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        if (safeIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[safeIndex]),
                                color = YatriColors.BrandAmber
                            )
                        }
                    },
                    containerColor = Color.Transparent
                ) {
                    itinerary.days.forEachIndexed { index, day ->
                        Tab(
                            selected = safeIndex == index,
                            onClick = { onSelectDay(index) },
                            text = {
                                Text(
                                    text = "Day ${day.dayNumber}",
                                    style = YatriTypographyTokens.TitleMedium,
                                    color = if (safeIndex == index) YatriColors.BrandAmber else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }

            // Day Theme & Summary
            val currentDay = itinerary.days[safeIndex]
            item {
                Column(modifier = Modifier.padding(vertical = YatriSpacing.xs)) {
                    Text(
                        text = currentDay.theme.ifBlank { "Day ${currentDay.dayNumber} Exploration" },
                        style = YatriTypographyTokens.HeadlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (currentDay.overview.isNotBlank()) {
                        Text(
                            text = currentDay.overview,
                            style = YatriTypographyTokens.BodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Timeline of Activities for Selected Day
            items(currentDay.activities) { activity ->
                ActivityTimelineItem(activity = activity)
            }
        }

        item {
            Spacer(modifier = Modifier.height(YatriSpacing.xxl))
        }
    }
}

// =====================================================================
// 4. Summary Card Component
// =====================================================================

@Composable
private fun ItinerarySummaryCard(itinerary: Itinerary) {
    YatriCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(YatriSpacing.m)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = itinerary.destinationName,
                        style = YatriTypographyTokens.HeadlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${itinerary.durationDays} Days Plan",
                        style = YatriTypographyTokens.BodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                YatriStatusPill(
                    label = "${itinerary.crowdAvoidanceRating.uppercase()} CROWD AVOIDANCE",
                    type = when (itinerary.crowdAvoidanceRating.uppercase()) {
                        "EXCELLENT", "HIGH" -> StatusPillType.CROWD_LOW
                        "GOOD", "MODERATE" -> StatusPillType.CROWD_MODERATE
                        else -> StatusPillType.CROWD_HIGH
                    }
                )
            }

            Spacer(modifier = Modifier.height(YatriSpacing.m))

            // Metrics row: Sustainability & Budget
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Sustainability Score",
                        style = YatriTypographyTokens.LabelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${itinerary.sustainabilityScore}/100",
                        style = YatriTypographyTokens.TitleLarge.copy(color = YatriColors.CrowdLow, fontWeight = FontWeight.Bold)
                    )
                }

                if (itinerary.totalEstimatedBudgetInr > 0) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Estimated Budget",
                            style = YatriTypographyTokens.LabelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = itinerary.formattedTotalBudget,
                            style = YatriTypographyTokens.TitleLarge.copy(color = YatriColors.BrandAmber, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

// =====================================================================
// 5. Activity Timeline Item Component
// =====================================================================

@Composable
private fun ActivityTimelineItem(activity: ActivitySlot) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = YatriSpacing.xs)
    ) {
        // Timeline indicator column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(YatriColors.BrandAmber)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(90.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        Spacer(modifier = Modifier.width(YatriSpacing.s))

        // Activity Card
        YatriCard(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.padding(YatriSpacing.m)) {
                // Time & Category Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = YatriColors.BrandAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(YatriSpacing.xs))
                        Text(
                            text = activity.timeSlot.ifBlank { activity.period },
                            style = YatriTypographyTokens.LabelSmall.copy(fontWeight = FontWeight.Bold),
                            color = YatriColors.BrandAmber
                        )
                    }

                    if (activity.category.isNotBlank()) {
                        Text(
                            text = activity.category.uppercase(),
                            style = YatriTypographyTokens.LabelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(YatriSpacing.xs))

                // Activity Title
                Text(
                    text = activity.title,
                    style = YatriTypographyTokens.TitleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Location & Duration
                val metaDetails = listOfNotNull(
                    activity.locationName.takeIf { it.isNotBlank() },
                    activity.durationLabel.takeIf { it.isNotBlank() }
                ).joinToString(" • ")

                if (metaDetails.isNotBlank()) {
                    Text(
                        text = metaDetails,
                        style = YatriTypographyTokens.BodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Description
                if (activity.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(YatriSpacing.xs))
                    Text(
                        text = activity.description,
                        style = YatriTypographyTokens.BodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Crowd forecast, Cost & Weather adaptation chips/badges
                if (activity.crowdForecast.isNotBlank() || activity.costEstimateInr > 0 || !activity.adaptationReason.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(YatriSpacing.s))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(YatriSpacing.s),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (activity.crowdForecast.isNotBlank()) {
                            YatriStatusPill(
                                label = activity.crowdForecast,
                                type = StatusPillType.CROWD_LOW
                            )
                        }

                        if (activity.costEstimateInr > 0) {
                            Text(
                                text = activity.formattedCost,
                                style = YatriTypographyTokens.LabelSmall.copy(fontWeight = FontWeight.Bold),
                                color = YatriColors.BrandAmber
                            )
                        }

                        if (!activity.adaptationReason.isNullOrBlank()) {
                            Text(
                                text = activity.adaptationReason,
                                style = YatriTypographyTokens.LabelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Tips if available
                if (!activity.travelTip.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(YatriSpacing.xs))
                    Text(
                        text = "💡 Tip: ${activity.travelTip}",
                        style = YatriTypographyTokens.BodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// =====================================================================
// 6. Error Content
// =====================================================================

@Composable
private fun ItineraryErrorContent(
    message: String,
    onRetry: () -> Unit,
    onEditPreferences: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(YatriSpacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Planning Error",
            tint = YatriColors.SosRedPrimary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(YatriSpacing.m))
        Text(
            text = "Planning Could Not Complete",
            style = YatriTypographyTokens.HeadlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(YatriSpacing.s))
        Text(
            text = message,
            style = YatriTypographyTokens.BodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(YatriSpacing.l))
        Row(horizontalArrangement = Arrangement.spacedBy(YatriSpacing.m)) {
            YatriButton(
                onClick = onRetry,
                variant = YatriButtonVariant.PRIMARY
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(YatriSpacing.xs))
                Text("Retry")
            }
            YatriButton(
                onClick = onEditPreferences,
                variant = YatriButtonVariant.SECONDARY
            ) {
                Text("Edit Preferences")
            }
        }
    }
}
