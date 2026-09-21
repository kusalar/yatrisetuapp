package com.yatrisetu.app.presentation.alternatives

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yatrisetu.app.domain.model.AlternativeDestination
import com.yatrisetu.app.domain.model.AlternativeWeatherInfo
import com.yatrisetu.app.domain.model.CrowdLevel
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

private val SampleAlternatives = listOf(
    AlternativeDestination(
        id = "kalimpong",
        name = "Kalimpong",
        tagline = "Ridge orchid sanctuaries, colonial gompas & quiet Teesta valley views",
        heroImage = "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=800&q=80",
        crowdScore = 42,
        crowdLevel = CrowdLevel.MEDIUM,
        similarityScore = 87,
        crowdReductionPercent = 52,
        distanceKm = 50f,
        estimatedCostPerDay = 2800,
        costDifferencePercent = 42,
        reasonsToRecommend = listOf("87% Cultural & Climatic Similarity", "42% Lower Daily Expense", "Healthy Homestay Capacity"),
        sharedHighlights = listOf("Kanchenjunga Views", "Heritage Monasteries", "Tea Culture"),
        capacityStatus = "HEALTHY",
        accessStatus = "OPEN",
        weather = AlternativeWeatherInfo("kalimpong", 16f, "Clear Mountain Sun", "REAL")
    ),
    AlternativeDestination(
        id = "rishop",
        name = "Rishop",
        tagline = "360-degree Kanchenjunga sunrise haven perched high at 8,500 ft",
        heroImage = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=80",
        crowdScore = 15,
        crowdLevel = CrowdLevel.LOW,
        similarityScore = 85,
        crowdReductionPercent = 83,
        distanceKm = 64f,
        estimatedCostPerDay = 2200,
        costDifferencePercent = 54,
        reasonsToRecommend = listOf("Unobstructed Sunrise Panorama", "Tranquil Silence", "Verified Panchayat Lodges"),
        sharedHighlights = listOf("Snow Peak Wall", "Alpine Hikes"),
        capacityStatus = "HEALTHY",
        accessStatus = "OPEN",
        weather = AlternativeWeatherInfo("rishop", 11f, "Crisp Mountain Air", "REAL")
    )
)

@Composable
fun AlternativesScreen(
    originId: String = "darjeeling",
    onNavigateBack: () -> Unit,
    onSelectAlternative: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val originName = originId.replaceFirstChar { it.uppercase() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = YatriSpacing.l),
        verticalArrangement = Arrangement.spacedBy(YatriSpacing.l)
    ) {
        item {
            Spacer(modifier = Modifier.height(YatriSpacing.s))
            Text(
                text = "Flow Management Advisor",
                style = YatriTypographyTokens.HeadlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Capacity-verified rural alternatives to unburden $originName",
                style = YatriTypographyTokens.BodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(SampleAlternatives, key = { it.id }) { alt ->
            YatriCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    // Image and Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(YatriRadius.Card))
                    ) {
                        AsyncImage(
                            model = alt.heroImage,
                            contentDescription = alt.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                        Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                            CrowdScoreBadge(score = alt.crowdScore, level = alt.crowdLevel)
                        }
                    }

                    Spacer(modifier = Modifier.height(YatriSpacing.m))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = alt.name,
                            style = YatriTypographyTokens.HeadlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        YatriStatusPill(
                            label = "${alt.similarityScore}% SIMILAR",
                            type = StatusPillType.CROWD_LOW
                        )
                    }

                    Spacer(modifier = Modifier.height(YatriSpacing.xs))
                    Text(
                        text = alt.tagline,
                        style = YatriTypographyTokens.BodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(YatriSpacing.m))

                    // Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricItem("Distance", "${alt.distanceKm.toInt()} km")
                        MetricItem("Savings", "${alt.costDifferencePercent}% less")
                        MetricItem("Road Access", alt.accessStatus)
                        MetricItem("Capacity", alt.capacityStatus)
                    }

                    Spacer(modifier = Modifier.height(YatriSpacing.m))

                    YatriButton(
                        onClick = { onSelectAlternative(alt.id) },
                        variant = YatriButtonVariant.PRIMARY,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Choose ${alt.name} & Plan Trip")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column {
        Text(text = label, style = YatriTypographyTokens.LabelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = YatriTypographyTokens.LabelLarge, color = MaterialTheme.colorScheme.primary)
    }
}
