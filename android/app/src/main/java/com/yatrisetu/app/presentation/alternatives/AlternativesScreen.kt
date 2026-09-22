package com.yatrisetu.app.presentation.alternatives

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.yatrisetu.app.data.repository.RepositoryProvider
import com.yatrisetu.app.domain.model.AlternativeDestination
import com.yatrisetu.app.domain.model.CrowdInfo
import com.yatrisetu.app.domain.model.CrowdLevel
import com.yatrisetu.app.presentation.common.CrowdGauge
import com.yatrisetu.app.presentation.common.CrowdScoreBadge
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
fun AlternativesScreen(
    originId: String = "darjeeling",
    onNavigateBack: () -> Unit,
    onSelectAlternative: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FlowAdvisorViewModel = viewModel(
        factory = FlowAdvisorViewModel.Factory(
            alternativesRepository = RepositoryProvider.getAlternativesRepository(LocalContext.current),
            crowdRepository = RepositoryProvider.getCrowdRepository(LocalContext.current),
            originDestinationId = originId
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val originName = uiState.originCrowdInfo?.destinationName
        ?: originId.replaceFirstChar { it.uppercase() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = YatriSpacing.l),
            verticalArrangement = Arrangement.spacedBy(YatriSpacing.l)
        ) {
            // Header Section
            item {
                Spacer(modifier = Modifier.height(YatriSpacing.s))
                FlowAdvisorHeader(
                    originName = originName,
                    isLive = uiState.alternativesData?.isLive ?: true,
                    freshnessLabel = uiState.alternativesData?.freshnessLabel
                )
            }

            // Current Destination Crowd Pressure Card
            item {
                CurrentDestinationPressureCard(
                    destinationName = originName,
                    crowdInfo = uiState.originCrowdInfo,
                    isLoading = uiState.isLoadingCrowd
                )
            }

            // Primary CTA: Find / Refresh Calmer Alternatives
            item {
                YatriButton(
                    onClick = { viewModel.loadAlternatives(originId, forceRefresh = true) },
                    variant = YatriButtonVariant.PRIMARY,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoadingAlternatives
                ) {
                    if (uiState.isLoadingAlternatives) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(YatriSpacing.s))
                        Text("Analyzing Network Alternatives...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(YatriSpacing.s))
                        Text(if (uiState.hasAlternatives) "Refresh Calmer Alternatives" else "Find Calmer Alternatives")
                    }
                }
            }

            // Comparison View if an alternative is selected
            if (uiState.selectedAlternative != null) {
                item {
                    AlternativeComparisonCard(
                        originName = originName,
                        originCrowd = uiState.originCrowdInfo,
                        alternative = uiState.selectedAlternative!!,
                        isAccepting = uiState.isAccepting,
                        onDismiss = { viewModel.dismissComparison() },
                        onExplore = {
                            viewModel.acceptAlternativeAndExplore(
                                alternative = uiState.selectedAlternative!!,
                                onNavigateToDestination = onSelectAlternative
                            )
                        }
                    )
                }
            }

            // Alternatives Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recommended Flow Alternatives",
                        style = YatriTypographyTokens.HeadlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (uiState.alternativesData != null) {
                        Text(
                            text = "${uiState.alternativesData!!.alternatives.size} available",
                            style = YatriTypographyTokens.LabelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Content: Loading Skeleton, Error, Empty, or List of Alternative Destination Cards
            when {
                uiState.isLoadingAlternatives && uiState.alternativesData == null -> {
                    items(2) {
                        AlternativeCardSkeleton()
                    }
                }

                uiState.errorMessage != null && uiState.alternativesData == null -> {
                    item {
                        FlowAdvisorErrorCard(
                            errorMessage = uiState.errorMessage!!,
                            onRetry = { viewModel.retry() }
                        )
                    }
                }

                uiState.alternativesData != null && uiState.alternativesData!!.alternatives.isEmpty() -> {
                    item {
                        FlowAdvisorEmptyCard(
                            originName = originName,
                            onRefresh = { viewModel.loadAlternatives(originId, forceRefresh = true) }
                        )
                    }
                }

                uiState.alternativesData != null -> {
                    items(
                        items = uiState.alternativesData!!.alternatives,
                        key = { it.id }
                    ) { alt ->
                        AlternativeDestinationCard(
                            alternative = alt,
                            isSelected = uiState.selectedAlternative?.id == alt.id,
                            onCompare = { viewModel.selectAlternativeForComparison(alt) },
                            onExplore = {
                                viewModel.acceptAlternativeAndExplore(
                                    alternative = alt,
                                    onNavigateToDestination = onSelectAlternative
                                )
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun FlowAdvisorHeader(
    originName: String,
    isLive: Boolean,
    freshnessLabel: String?
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Flow Advisor",
                    style = YatriTypographyTokens.DisplaySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Find calmer places without losing the experience",
                    style = YatriTypographyTokens.BodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!isLive && freshnessLabel != null) {
                YatriStatusPill(
                    label = "OFFLINE CACHE",
                    type = StatusPillType.NEUTRAL,
                    contentDesc = "Offline cached recommendations"
                )
            }
        }
        if (!isLive && freshnessLabel != null) {
            Spacer(modifier = Modifier.height(YatriSpacing.xs))
            Text(
                text = freshnessLabel,
                style = YatriTypographyTokens.LabelSmall,
                color = YatriColors.CrowdMedium
            )
        }
    }
}

@Composable
private fun CurrentDestinationPressureCard(
    destinationName: String,
    crowdInfo: CrowdInfo?,
    isLoading: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    YatriCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CURRENT DESTINATION",
                        style = YatriTypographyTokens.LabelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = destinationName,
                        style = YatriTypographyTokens.HeadlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (crowdInfo != null) {
                    val pillType = when (crowdInfo.crowdLevel) {
                        CrowdLevel.LOW -> StatusPillType.CROWD_LOW
                        CrowdLevel.MEDIUM -> StatusPillType.CROWD_MEDIUM
                        CrowdLevel.HIGH -> StatusPillType.CROWD_HIGH
                        CrowdLevel.VERY_HIGH -> StatusPillType.CROWD_VERY_HIGH
                    }
                    YatriStatusPill(
                        label = "${crowdInfo.crowdLevel.displayName} PRESSURE",
                        type = pillType,
                        contentDesc = "Current crowd pressure: ${crowdInfo.crowdLevel.displayName}"
                    )
                }
            }

            Spacer(modifier = Modifier.height(YatriSpacing.m))

            if (isLoading && crowdInfo == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                }
            } else if (crowdInfo != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(YatriSpacing.m)
                ) {
                    Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                        CrowdGauge(
                            score = crowdInfo.crowdScore,
                            level = crowdInfo.crowdLevel,
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = crowdInfo.summary.ifBlank { "High tourist footfall and vehicular convergence detected." },
                            style = YatriTypographyTokens.BodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (crowdInfo.liveTrafficStatus.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Traffic: ${crowdInfo.liveTrafficStatus} · Hotel Occupancy: ${crowdInfo.hotelOccupancyRate.ifBlank { "High" }}",
                                style = YatriTypographyTokens.LabelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // "Why this is crowded" section
                if (crowdInfo.whyCrowded.isNotEmpty() || crowdInfo.factors.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(YatriSpacing.s))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(YatriSpacing.s))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Why this is crowded",
                            style = YatriTypographyTokens.LabelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (expanded) "Hide details" else "View factors (${crowdInfo.whyCrowded.size + crowdInfo.factors.size})",
                            style = YatriTypographyTokens.LabelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    AnimatedVisibility(visible = expanded) {
                        Column(modifier = Modifier.padding(top = YatriSpacing.s)) {
                            crowdInfo.whyCrowded.forEach { reason ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "• ",
                                        style = YatriTypographyTokens.BodyMedium,
                                        color = YatriColors.CrowdHigh
                                    )
                                    Text(
                                        text = reason,
                                        style = YatriTypographyTokens.BodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (crowdInfo.peakVisitingHours.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Peak hours: ${crowdInfo.peakVisitingHours}",
                                    style = YatriTypographyTokens.LabelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlternativeDestinationCard(
    alternative: AlternativeDestination,
    isSelected: Boolean,
    onCompare: () -> Unit,
    onExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)

    YatriCard(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(YatriRadius.Card)
            )
    ) {
        Column {
            // Hero Image with Floating Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = YatriRadius.Card, topEnd = YatriRadius.Card))
            ) {
                AsyncImage(
                    model = alternative.heroImage,
                    contentDescription = alternative.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top End: Crowd Score Badge
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)) {
                    CrowdScoreBadge(
                        score = alternative.crowdScore,
                        level = alternative.crowdLevel
                    )
                }

                // Top Start: Crowd Reduction Pill (Authority: backend provided)
                Box(modifier = Modifier.align(Alignment.TopStart).padding(10.dp)) {
                    YatriStatusPill(
                        label = alternative.crowdReductionLabel,
                        type = StatusPillType.CROWD_LOW,
                        contentDesc = "${alternative.crowdReductionPercent} percent less crowded"
                    )
                }
            }

            Spacer(modifier = Modifier.height(YatriSpacing.m))

            // Visual Priority 1: Destination Name & Region
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alternative.name,
                        style = YatriTypographyTokens.HeadlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (alternative.state.isNotBlank() || alternative.tagline.isNotBlank()) {
                        Text(
                            text = alternative.tagline.ifBlank { alternative.state },
                            style = YatriTypographyTokens.BodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }

                // Visual Priority 4: Similarity Match Pill
                YatriStatusPill(
                    label = alternative.similarityLabel,
                    type = StatusPillType.VERIFIED,
                    contentDesc = "${alternative.similarityScore} percent similar mountain experience"
                )
            }

            Spacer(modifier = Modifier.height(YatriSpacing.m))

            // Visual Priority 6: Reason for suggestion (backend explainability)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = alternative.primaryReason,
                        style = YatriTypographyTokens.BodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(YatriSpacing.m))

            // Secondary Conditions & Key Telemetry Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AlternativeMetric(
                    label = "Distance",
                    value = alternative.distanceLabel
                )
                AlternativeMetric(
                    label = "Est. Cost",
                    value = alternative.dailyCostLabel
                )
                AlternativeMetric(
                    label = "Corridor",
                    value = alternative.accessStatus
                )
                AlternativeMetric(
                    label = "Capacity",
                    value = alternative.capacityStatus
                )
            }

            // Weather observation pill if available
            if (alternative.weather != null) {
                Spacer(modifier = Modifier.height(YatriSpacing.s))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "Current Weather: ",
                        style = YatriTypographyTokens.LabelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${alternative.weather.temperature.toInt()}°C · ${alternative.weather.condition}",
                        style = YatriTypographyTokens.LabelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(YatriSpacing.m))

            // Action Buttons: Compare Conditions vs Choose & Explore
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(YatriSpacing.s)
            ) {
                YatriButton(
                    onClick = onCompare,
                    variant = YatriButtonVariant.SECONDARY,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isSelected) "Comparing" else "Compare")
                }

                YatriButton(
                    onClick = onExplore,
                    variant = YatriButtonVariant.PRIMARY,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Explore")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AlternativeMetric(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = YatriTypographyTokens.LabelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = YatriTypographyTokens.LabelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AlternativeComparisonCard(
    originName: String,
    originCrowd: CrowdInfo?,
    alternative: AlternativeDestination,
    isAccepting: Boolean,
    onDismiss: () -> Unit,
    onExplore: () -> Unit
) {
    YatriCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(YatriRadius.Card)
            )
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FLOW COMPARISON",
                        style = YatriTypographyTokens.LabelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$originName vs ${alternative.name}",
                        style = YatriTypographyTokens.HeadlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss comparison",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(YatriSpacing.s))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(YatriSpacing.m))

            // Structured Comparison Table/Rows
            ComparisonRow(
                factor = "Crowd Score",
                currentVal = "${originCrowd?.crowdScore ?: 75} (${originCrowd?.crowdLevel?.displayName ?: "HIGH"})",
                altVal = "${alternative.crowdScore} (${alternative.crowdLevel.displayName})",
                highlightAlt = true
            )

            ComparisonRow(
                factor = "Crowd Reduction",
                currentVal = "—",
                altVal = alternative.crowdReductionLabel,
                highlightAlt = true
            )

            ComparisonRow(
                factor = "Similarity Match",
                currentVal = "Original",
                altVal = alternative.similarityLabel,
                highlightAlt = false
            )

            ComparisonRow(
                factor = "Distance",
                currentVal = "Current",
                altVal = alternative.distanceLabel,
                highlightAlt = false
            )

            ComparisonRow(
                factor = "Daily Cost",
                currentVal = "High season rates",
                altVal = "${alternative.dailyCostLabel} ${if (alternative.costDifferencePercent > 0) "(-${alternative.costDifferencePercent}%)" else ""}",
                highlightAlt = alternative.costDifferencePercent > 0
            )

            ComparisonRow(
                factor = "Road Access",
                currentVal = originCrowd?.liveTrafficStatus ?: "Congested",
                altVal = alternative.accessStatus,
                highlightAlt = alternative.accessStatus == "OPEN"
            )

            Spacer(modifier = Modifier.height(YatriSpacing.m))

            YatriButton(
                onClick = onExplore,
                variant = YatriButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isAccepting
            ) {
                if (isAccepting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(YatriSpacing.s))
                    Text("Recording Choice...")
                } else {
                    Text("Choose ${alternative.name} & Explore")
                    Spacer(modifier = Modifier.width(YatriSpacing.s))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ComparisonRow(
    factor: String,
    currentVal: String,
    altVal: String,
    highlightAlt: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = factor,
            style = YatriTypographyTokens.BodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = currentVal,
            style = YatriTypographyTokens.BodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = altVal,
            style = YatriTypographyTokens.BodySmall,
            color = if (highlightAlt) YatriColors.CrowdLow else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (highlightAlt) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1.2f)
        )
    }
}

@Composable
private fun AlternativeCardSkeleton() {
    YatriCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(YatriRadius.Card))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.height(YatriSpacing.m))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(20.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.height(YatriSpacing.s))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.height(YatriSpacing.m))
        }
    }
}

@Composable
private fun FlowAdvisorErrorCard(
    errorMessage: String,
    onRetry: () -> Unit
) {
    YatriCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(YatriSpacing.m),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = YatriColors.CrowdHigh,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(YatriSpacing.s))
            Text(
                text = "Unable to load alternatives",
                style = YatriTypographyTokens.HeadlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = YatriTypographyTokens.BodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(YatriSpacing.m))
            YatriButton(
                onClick = onRetry,
                variant = YatriButtonVariant.PRIMARY
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Retry")
            }
        }
    }
}

@Composable
private fun FlowAdvisorEmptyCard(
    originName: String,
    onRefresh: () -> Unit
) {
    YatriCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(YatriSpacing.l),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(YatriSpacing.m))
            Text(
                text = "No suitable alternatives right now",
                style = YatriTypographyTokens.HeadlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(YatriSpacing.xs))
            Text(
                text = "Current capacity and corridor conditions across the network do not meet criteria for a safer flow shift from $originName.",
                style = YatriTypographyTokens.BodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(YatriSpacing.l))
            YatriButton(
                onClick = onRefresh,
                variant = YatriButtonVariant.SECONDARY
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Refresh Telemetry")
            }
        }
    }
}
