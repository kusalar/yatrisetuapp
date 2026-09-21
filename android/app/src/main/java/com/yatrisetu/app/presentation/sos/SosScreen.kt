package com.yatrisetu.app.presentation.sos

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatrisetu.app.domain.model.OfficialContact
import com.yatrisetu.app.presentation.common.StatusPillType
import com.yatrisetu.app.presentation.common.YatriButton
import com.yatrisetu.app.presentation.common.YatriButtonVariant
import com.yatrisetu.app.presentation.common.YatriCard
import com.yatrisetu.app.presentation.common.YatriStatusPill
import com.yatrisetu.app.presentation.theme.YatriColors
import com.yatrisetu.app.presentation.theme.YatriRadius
import com.yatrisetu.app.presentation.theme.YatriSpacing
import com.yatrisetu.app.presentation.theme.YatriTypographyTokens

private val OfficialHelplines = listOf(
    OfficialContact("National Emergency Helpline", "112", "All-India unified police, medical & fire response", "National"),
    OfficialContact("Tourist Safety Infoline", "1363", "Ministry of Tourism 24/7 multi-language guidance", "National"),
    OfficialContact("Women Helpline", "1091", "Dedicated round-the-clock emergency desk", "National"),
    OfficialContact("Disaster Management (SEOC)", "1070", "State emergency operations center for landslides/weather", "West Bengal")
)

@Composable
fun SosScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = YatriSpacing.l),
        verticalArrangement = Arrangement.spacedBy(YatriSpacing.l)
    ) {
        item {
            Spacer(modifier = Modifier.height(YatriSpacing.s))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Emergency SOS",
                    style = YatriTypographyTokens.HeadlineLarge,
                    color = YatriColors.SosRedPrimary
                )
                YatriStatusPill(label = "24/7 ACTIVE", type = StatusPillType.CROWD_VERY_HIGH)
            }
            Text(
                text = "Instant traveler safety beacon & verified government helpline directory",
                style = YatriTypographyTokens.BodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // SOS Trigger Card
        item {
            YatriCard(
                backgroundColor = YatriColors.SosRedPrimary.copy(alpha = 0.05f),
                border = androidx.compose.foundation.BorderStroke(1.dp, YatriColors.SosRedPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(YatriSpacing.m)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(YatriColors.SosRedPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Emergency Alert",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(YatriSpacing.m))

                    Text(
                        text = "Emergency Distress Beacon",
                        style = YatriTypographyTokens.HeadlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Broadcasting will alert local Yatri Mitra volunteer responders and transmit GPS telemetry to the district safety desk.",
                        style = YatriTypographyTokens.BodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(YatriSpacing.l))

                    YatriButton(
                        onClick = { /* Implemented in dedicated Safety phase */ },
                        variant = YatriButtonVariant.CRITICAL,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Hold SOS to Broadcast Distress")
                    }
                }
            }
        }

        // Helplines Directory
        item {
            Text(
                text = "Official National Helplines",
                style = YatriTypographyTokens.HeadlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(OfficialHelplines, key = { it.phoneNumber }) { contact ->
            YatriCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = contact.serviceName,
                            style = YatriTypographyTokens.TitleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = contact.description,
                            style = YatriTypographyTokens.BodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(YatriSpacing.m))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(YatriRadius.Pill))
                            .background(YatriColors.CrowdLowBg)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call ${contact.phoneNumber}",
                            tint = YatriColors.CrowdLow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = contact.phoneNumber,
                            style = YatriTypographyTokens.LabelLarge.copy(fontSize = 14.sp),
                            color = YatriColors.CrowdLow
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
