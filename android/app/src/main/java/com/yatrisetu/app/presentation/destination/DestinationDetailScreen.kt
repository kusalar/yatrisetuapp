package com.yatrisetu.app.presentation.destination

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.yatrisetu.app.data.repository.RepositoryProvider
import com.yatrisetu.app.domain.model.Attraction
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
fun DestinationDetailScreen(
    destinationId: String,
    onNavigateBack: () -> Unit,
    onNavigateToCrowd: (String) -> Unit,
    onNavigateToAlternatives: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DestinationDetailViewModel = viewModel(
        factory = DestinationDetailViewModel.Factory(
            destinationRepository = RepositoryProvider.getDestinationRepository(LocalContext.current),
            crowdRepository = RepositoryProvider.getCrowdRepository(LocalContext.current),
            destinationId = destinationId
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val fallbackName = destinationId.replaceFirstChar { it.uppercase() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isLoading && uiState.destination == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = YatriColors.BrandAmber)
            }
        } else if (uiState.errorMessage != null && uiState.destination == null) {
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
                    text = uiState.errorMessage ?: "Failed to load destination details",
                    style = YatriTypographyTokens.BodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(YatriSpacing.l))
                YatriButton(onClick = { viewModel.refresh() }) {
                    Text("Retry")
                }
            }
        } else {
            val dest = uiState.destination ?: return
            val crowd = uiState.crowdInfo
            val score = crowd?.crowdScore ?: dest.baseCrowdScore
            val level = crowd?.crowdLevel ?: CrowdLevel.fromScore(score)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // Top App Row with Back & Refresh
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = YatriSpacing.l, vertical = YatriSpacing.s),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
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

                // Hero Image
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(horizontal = YatriSpacing.l)
                            .clip(RoundedCornerShape(YatriRadius.Card))
                    ) {
                        AsyncImage(
                            model = dest.heroImage.ifBlank {
                                "https://images.unsplash.com/photo-1544644181-1484b3fdfc62?auto=format&fit=crop&w=800&q=80"
                            },
                            contentDescription = dest.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Title & Subtitle Header
                item {
                    Column(modifier = Modifier.padding(horizontal = YatriSpacing.l, vertical = YatriSpacing.m)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = dest.name,
                                    style = YatriTypographyTokens.DisplayLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "${dest.region} • ${dest.state}",
                                    style = YatriTypographyTokens.BodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            YatriStatusPill(
                                label = "${level.displayName} · $score",
                                type = when (level) {
                                    CrowdLevel.LOW -> StatusPillType.CROWD_LOW
                                    CrowdLevel.MEDIUM -> StatusPillType.CROWD_MODERATE
                                    CrowdLevel.HIGH -> StatusPillType.CROWD_HIGH
                                    CrowdLevel.VERY_HIGH -> StatusPillType.CROWD_VERY_HIGH
                                }
                            )
                        }

                        if (dest.tagline.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dest.tagline,
                                style = YatriTypographyTokens.BodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                color = YatriColors.BrandAmber
                            )
                        }
                    }
                }

                // Live Crowd Status Card with Embedded CrowdGauge
                item {
                    Box(modifier = Modifier.padding(horizontal = YatriSpacing.l)) {
                        YatriCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(YatriSpacing.m)
                            ) {
                                CrowdGauge(
                                    score = score,
                                    level = level,
                                    size = 160.dp
                                )
                                Spacer(modifier = Modifier.height(YatriSpacing.s))
                                Text(
                                    text = crowd?.summary ?: "Real-time multi-signal telemetry active.",
                                    style = YatriTypographyTokens.BodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(YatriSpacing.m))
                                YatriButton(
                                    onClick = { onNavigateToCrowd(dest.id) },
                                    variant = YatriButtonVariant.SECONDARY,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("View Detailed Crowd Drivers & Telemetry")
                                }
                            }
                        }
                    }
                }

                // Destination Description
                item {
                    Column(modifier = Modifier.padding(horizontal = YatriSpacing.l, vertical = YatriSpacing.m)) {
                        Text(
                            text = "About",
                            style = YatriTypographyTokens.HeadlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(YatriSpacing.xs))
                        Text(
                            text = dest.description,
                            style = YatriTypographyTokens.BodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Attributes & Altitude Card
                item {
                    Box(modifier = Modifier.padding(horizontal = YatriSpacing.l)) {
                        YatriCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(YatriSpacing.m)) {
                                Text(
                                    text = "Destination Highlights & Climate",
                                    style = YatriTypographyTokens.TitleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(YatriSpacing.s))
                                AttributeRow(label = "Altitude", value = "${dest.attributes.altitudeFt} ft")
                                AttributeRow(label = "Climate", value = dest.attributes.climate)
                                AttributeRow(label = "Culture", value = dest.attributes.culture)
                                AttributeRow(label = "Est. Daily Budget", value = "₹${dest.attributes.avgCostPerDayInr} / day")
                                AttributeRow(label = "Accessibility", value = dest.attributes.accessibility)
                            }
                        }
                    }
                }

                // Highlights Bullet Points
                if (dest.highlights.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = YatriSpacing.l, vertical = YatriSpacing.m)) {
                            Text(
                                text = "Key Highlights",
                                style = YatriTypographyTokens.HeadlineMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(YatriSpacing.xs))
                            dest.highlights.forEach { highlight ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = YatriColors.CrowdLow,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(YatriSpacing.s))
                                    Text(
                                        text = highlight,
                                        style = YatriTypographyTokens.BodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Attractions List
                if (dest.attractions.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = YatriSpacing.l, vertical = YatriSpacing.m)) {
                            Text(
                                text = "Local Attractions (${dest.attractions.size})",
                                style = YatriTypographyTokens.HeadlineMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(YatriSpacing.s))
                        }
                    }

                    items(dest.attractions, key = { it.id }) { attraction ->
                        Box(modifier = Modifier.padding(horizontal = YatriSpacing.l, vertical = YatriSpacing.xs)) {
                            AttractionCard(attraction = attraction)
                        }
                    }
                }

                // Find Alternatives Button
                item {
                    Spacer(modifier = Modifier.height(YatriSpacing.m))
                    Box(modifier = Modifier.padding(horizontal = YatriSpacing.l)) {
                        YatriButton(
                            onClick = { onNavigateToAlternatives(dest.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Find Low-Crowd Alternatives")
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
private fun AttributeRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = YatriTypographyTokens.BodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = YatriTypographyTokens.BodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AttractionCard(attraction: Attraction) {
    YatriCard(modifier = Modifier.fillMaxWidth(), elevation = 1.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(YatriSpacing.s),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(YatriRadius.Button))
            ) {
                AsyncImage(
                    model = attraction.imageUrl.ifBlank {
                        "https://images.unsplash.com/photo-1544644181-1484b3fdfc62?auto=format&fit=crop&w=400&q=80"
                    },
                    contentDescription = attraction.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(YatriSpacing.m))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attraction.name,
                    style = YatriTypographyTokens.TitleLarge.copy(fontSize = 15.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = attraction.category,
                    style = YatriTypographyTokens.BodyMedium.copy(fontSize = 12.sp),
                    color = YatriColors.BrandAmber
                )
                Text(
                    text = "${attraction.visitDurationHrs} hrs • Best: ${attraction.bestTime}",
                    style = YatriTypographyTokens.BodyMedium.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            YatriStatusPill(
                label = attraction.crowdDensity.uppercase(),
                type = when (attraction.crowdDensity.uppercase()) {
                    "LOW" -> StatusPillType.CROWD_LOW
                    "HIGH" -> StatusPillType.CROWD_HIGH
                    else -> StatusPillType.CROWD_MODERATE
                }
            )
        }
    }
}
