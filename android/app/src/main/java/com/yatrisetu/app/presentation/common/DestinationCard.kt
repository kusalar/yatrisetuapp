package com.yatrisetu.app.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yatrisetu.app.domain.model.CrowdLevel
import com.yatrisetu.app.domain.model.DestinationSummary
import com.yatrisetu.app.presentation.theme.YatriRadius
import com.yatrisetu.app.presentation.theme.YatriSpacing
import com.yatrisetu.app.presentation.theme.YatriTypographyTokens

@Composable
fun DestinationCard(
    destination: DestinationSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    YatriCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            // Destination Image with Floating Crowd Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(YatriRadius.Card))
            ) {
                AsyncImage(
                    model = destination.heroImage,
                    contentDescription = destination.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                // Overlay Crowd Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(YatriSpacing.s)
                ) {
                    CrowdScoreBadge(
                        score = destination.crowdScore,
                        level = destination.crowdLevel
                    )
                }
            }

            Spacer(modifier = Modifier.height(YatriSpacing.m))

            // Title & Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = destination.name,
                    style = YatriTypographyTokens.HeadlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = destination.state,
                        style = YatriTypographyTokens.LabelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(YatriSpacing.xs))

            // Tagline
            Text(
                text = destination.tagline,
                style = YatriTypographyTokens.BodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(YatriSpacing.m))

            // Cost & Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${destination.avgCostPerDayInr} / day",
                    style = YatriTypographyTokens.LabelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                if (destination.tags.isNotEmpty()) {
                    YatriStatusPill(
                        label = destination.tags.first(),
                        type = StatusPillType.NEUTRAL
                    )
                }
            }
        }
    }
}
