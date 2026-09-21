package com.yatrisetu.app.presentation.crowd

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatrisetu.app.data.repository.RepositoryProvider
import com.yatrisetu.app.domain.model.CrowdFactor
import com.yatrisetu.app.domain.model.CrowdInfo
import com.yatrisetu.app.domain.model.CrowdLevel
import com.yatrisetu.app.presentation.common.CrowdGauge
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
fun CrowdIntelligenceScreen(
    destinationId: String,
    onNavigateBack: () -> Unit,
    onNavigateToAlternatives: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CrowdViewModel = viewModel(
        factory = CrowdViewModel.Factory(
            RepositoryProvider.getCrowdRepository(LocalContext.current),
            destinationId
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val destinationName = uiState.crowdInfo?.destinationName
        ?: destinationId.replaceFirstChar { it.uppercase() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isLoading && uiState.crowdInfo == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = YatriColors.BrandAmber)
            }
        } else if (uiState.errorMessage != null && uiState.crowdInfo == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(YatriSpacing.l),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = YatriColors.SosRedPrimary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(YatriSpacing.m))
                Text(
                    text = uiState.errorMessage ?: "Failed to connect to Crowd Engine",
                    style = YatriTypographyTokens.BodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(YatriSpacing.l))
                YatriButton(onClick = { viewModel.refresh() }) {
                    Text("Retry Telemetry")
                }
            }
        } else {
            val info = uiState.crowdInfo ?: return

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = YatriSpacing.l,
                    end = YatriSpacing.l,
                    top = YatriSpacing.m,
                    bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(YatriSpacing.l)
            ) {
                // Header Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Column {
                                Text(
                                    text = "$destinationName Crowd",
                                    style = YatriTypographyTokens.HeadlineLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = info.provenanceLabel ?: "Canonical Crowd Engine V2",
                                    style = YatriTypographyTokens.BodyMedium.copy(fontSize = 12.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.refresh() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = YatriColors.BrandAmber
                            )
                        }
                    }
                }

                // Freshness Status Pill
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        YatriStatusPill(
                            label = if (info.isLive) "LIVE TELEMETRY" else "OFFLINE CACHE",
                            type = if (info.isLive) StatusPillType.CROWD_LOW else StatusPillType.CROWD_MODERATE
                        )
                        Text(
                            text = info.freshnessLabel,
                            style = YatriTypographyTokens.BodyMedium.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Main Circular Gauge Card
                item {
                    YatriCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(YatriSpacing.m)
                        ) {
                            CrowdGauge(
                                score = info.crowdScore,
                                level = info.crowdLevel,
                                size = 180.dp
                            )
                            Spacer(modifier = Modifier.height(YatriSpacing.s))
                            Text(
                                text = "Authoritative Score: ${info.crowdScore}/100 • ${info.crowdLevel.displayName}",
                                style = YatriTypographyTokens.TitleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = info.summary,
                                style = YatriTypographyTokens.BodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Operational Key Metrics Grid
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(YatriSpacing.s)
                    ) {
                        MetricCard(
                            label = "Traffic Flow",
                            value = info.liveTrafficStatus.ifBlank { "Moderate" },
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            label = "Hotel Density",
                            value = info.hotelOccupancyRate.ifBlank { "75%" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(YatriSpacing.s)
                    ) {
                        MetricCard(
                            label = "Peak Visiting Hours",
                            value = info.peakVisitingHours.ifBlank { "10 AM - 4 PM" },
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            label = "Best Calm Window",
                            value = info.bestTimeToVisitToday.ifBlank { "Early Morning" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Why Congested (Top Drivers)
                if (info.whyCrowded.isNotEmpty()) {
                    item {
                        YatriCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(YatriSpacing.m)) {
                                Text(
                                    text = "Why is it crowded?",
                                    style = YatriTypographyTokens.HeadlineMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(YatriSpacing.s))
                                info.whyCrowded.forEach { reason ->
                                    ExplainabilityBullet(text = reason)
                                }
                            }
                        }
                    }
                }

                // Bottleneck Checkpoints
                if (info.bottlenecks.isNotEmpty()) {
                    item {
                        YatriCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(YatriSpacing.m)) {
                                Text(
                                    text = "Active Chokepoints",
                                    style = YatriTypographyTokens.HeadlineMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(YatriSpacing.s))
                                info.bottlenecks.forEach { point ->
                                    ExplainabilityBullet(text = point)
                                }
                            }
                        }
                    }
                }

                // Signal Pressure Drivers (Directly from backend factors, no fabricated math)
                if (info.factors.isNotEmpty()) {
                    item {
                        YatriCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(YatriSpacing.m)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Pressure Drivers Breakdown",
                                        style = YatriTypographyTokens.HeadlineMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${info.factors.size} Signals",
                                        style = YatriTypographyTokens.LabelLarge.copy(fontSize = 11.sp),
                                        color = YatriColors.BrandAmber
                                    )
                                }
                                Spacer(modifier = Modifier.height(YatriSpacing.m))

                                info.factors.forEach { factor ->
                                    PressureDriverRow(factor = factor)
                                    Spacer(modifier = Modifier.height(YatriSpacing.s))
                                }
                            }
                        }
                    }
                }

                // Alternative Routing Call-to-Action
                item {
                    YatriCard(
                        backgroundColor = YatriColors.BrandAmber.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            YatriColors.BrandAmber.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(YatriSpacing.m),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "High Crowd Pressure Detected",
                                style = YatriTypographyTokens.HeadlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Discover serene, low-crowd alternative Himalayan villages with matching cultural attractions and panchayat homestays.",
                                style = YatriTypographyTokens.BodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(YatriSpacing.m))
                            YatriButton(
                                onClick = { onNavigateToAlternatives(destinationId) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Find Low-Crowd Alternatives")
                            }
                        }
                    }
                }
            }
        }

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
private fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    YatriCard(modifier = modifier, elevation = 1.dp) {
        Column(modifier = Modifier.padding(YatriSpacing.s)) {
            Text(
                text = label,
                style = YatriTypographyTokens.BodyMedium.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = YatriTypographyTokens.LabelLarge.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ExplainabilityBullet(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(YatriColors.BrandAmber)
        )
        Spacer(modifier = Modifier.width(YatriSpacing.s))
        Text(
            text = text,
            style = YatriTypographyTokens.BodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PressureDriverRow(factor: CrowdFactor) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = factor.name,
                style = YatriTypographyTokens.BodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Score: ${factor.rawValue.toInt()} • Weight: ${factor.weightPercentage}%",
                style = YatriTypographyTokens.LabelLarge.copy(fontSize = 11.sp),
                color = YatriColors.BrandAmber
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (factor.rawValue / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(YatriRadius.Pill)),
            color = when {
                factor.rawValue >= 80 -> YatriColors.CrowdVeryHigh
                factor.rawValue >= 60 -> YatriColors.CrowdHigh
                factor.rawValue >= 40 -> YatriColors.CrowdMedium
                else -> YatriColors.CrowdLow
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        if (factor.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = factor.description,
                style = YatriTypographyTokens.BodyMedium.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
