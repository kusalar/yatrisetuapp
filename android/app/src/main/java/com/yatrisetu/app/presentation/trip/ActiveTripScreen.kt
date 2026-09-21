package com.yatrisetu.app.presentation.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yatrisetu.app.domain.model.TripDetails
import com.yatrisetu.app.presentation.common.StatusPillType
import com.yatrisetu.app.presentation.common.YatriButton
import com.yatrisetu.app.presentation.common.YatriButtonVariant
import com.yatrisetu.app.presentation.common.YatriCard
import com.yatrisetu.app.presentation.common.YatriStatusPill
import com.yatrisetu.app.presentation.theme.YatriSpacing
import com.yatrisetu.app.presentation.theme.YatriTypographyTokens

private val SampleTrip = TripDetails(
    tripId = "YS-BK-7492A",
    destinationId = "kalimpong",
    destinationName = "Kalimpong",
    homestayName = "Pineview Orchid Retreat & Homestay",
    dates = "Oct 12 - Oct 15, 2026",
    status = "Active Journey",
    travelersCount = 2,
    digitalPassCode = "YS-PASS-7492A",
    weatherAlert = "Pleasant Mountain Sun: 14°C - 21°C. Light evening chill.",
    hostSupportNumber = "+91 98320 87123 (Pemba Sherpa)",
    checkInLocation = "Atisha Road, Upper Cart Road, Kalimpong",
    packingChecklist = listOf(
        "Light fleece jacket for ridge breezes",
        "Trekking sneakers for pine & orchid trails",
        "Refillable canteen (plastic-free village zone)",
        "Government Photo ID for Forest Checkpost",
        "Digital Yatri Setu Travel Pass saved offline"
    )
)

@Composable
fun ActiveTripScreen(
    tripId: String = "YS-BK-7492A",
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
                    text = "Active Trip Dashboard",
                    style = YatriTypographyTokens.HeadlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                YatriStatusPill(label = SampleTrip.status, type = StatusPillType.AVAILABLE)
            }
            Text(
                text = "${SampleTrip.destinationName} • ${SampleTrip.dates}",
                style = YatriTypographyTokens.BodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Digital Travel Pass Card
        item {
            YatriCard(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    YatriStatusPill(label = "DIGITAL TRAVEL PASS", type = StatusPillType.VERIFIED)
                    Spacer(modifier = Modifier.height(YatriSpacing.s))
                    Text(
                        text = SampleTrip.digitalPassCode,
                        style = YatriTypographyTokens.HeadlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Verified entry permit for Forest checkposts & Homestay check-in",
                        style = YatriTypographyTokens.BodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(YatriSpacing.m))
                    Text(
                        text = "[ Offline QR Permit Cached ]",
                        style = YatriTypographyTokens.LabelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Weather Advisory
        item {
            YatriCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "Mountain Weather Advisory",
                        style = YatriTypographyTokens.TitleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(YatriSpacing.xs))
                    Text(
                        text = SampleTrip.weatherAlert,
                        style = YatriTypographyTokens.BodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Packing Checklist
        item {
            YatriCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "Himalayan Packing Checklist",
                        style = YatriTypographyTokens.TitleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(YatriSpacing.s))
                    SampleTrip.packingChecklist.forEach { item ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("✓ ", color = MaterialTheme.colorScheme.primary, style = YatriTypographyTokens.TitleMedium)
                            Text(text = item, style = YatriTypographyTokens.BodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
