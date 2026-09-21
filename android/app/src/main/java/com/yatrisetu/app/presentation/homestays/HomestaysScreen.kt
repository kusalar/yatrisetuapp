package com.yatrisetu.app.presentation.homestays

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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yatrisetu.app.domain.model.Homestay
import com.yatrisetu.app.domain.model.HostProfile
import com.yatrisetu.app.presentation.common.StatusPillType
import com.yatrisetu.app.presentation.common.YatriButton
import com.yatrisetu.app.presentation.common.YatriButtonVariant
import com.yatrisetu.app.presentation.common.YatriCard
import com.yatrisetu.app.presentation.common.YatriStatusPill
import com.yatrisetu.app.presentation.theme.YatriRadius
import com.yatrisetu.app.presentation.theme.YatriSpacing
import com.yatrisetu.app.presentation.theme.YatriTypographyTokens

private val SampleHomestays = listOf(
    Homestay(
        id = "hs-kalimpong-01",
        destinationId = "kalimpong",
        destinationName = "Kalimpong",
        title = "Pineview Orchid Retreat & Homestay",
        tagline = "Family-run heritage cottage overlooking Kanchenjunga and rare orchids",
        address = "Atisha Road, Upper Cart Road, Kalimpong",
        pricePerNightInr = 2400,
        rating = 4.9f,
        reviewsCount = 84,
        roomType = "Wooden Suite with Valley Balcony",
        maxGuests = 3,
        amenities = listOf("Organic Meals", "Hot Water", "High-Speed Wi-Fi", "Orchid Garden"),
        images = listOf("https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80"),
        host = HostProfile("Pemba & Choden Sherpa", "", 7, listOf("English", "Hindi", "Nepali"), "", true),
        verified = true
    ),
    Homestay(
        id = "hs-kalimpong-02",
        destinationId = "kalimpong",
        destinationName = "Kalimpong",
        title = "Deolo Vista Farmstay",
        tagline = "Quiet farm sanctuary with fresh dairy, mountain honey and Teesta valley views",
        address = "Deolo Hills, Near Water Reservoir, Kalimpong",
        pricePerNightInr = 2200,
        rating = 4.8f,
        reviewsCount = 62,
        roomType = "Deluxe Mountain View Room",
        maxGuests = 2,
        amenities = listOf("Farm Breakfast", "Solar Heating", "Trek Guide"),
        images = listOf("https://images.unsplash.com/photo-1587061949409-02df41d5e562?auto=format&fit=crop&w=800&q=80"),
        host = HostProfile("Lhamu Bhutia", "", 5, listOf("Hindi", "English"), "", true),
        verified = true
    )
)

@Composable
fun HomestaysScreen(
    onNavigateBack: () -> Unit,
    onReserveHomestay: (String) -> Unit,
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
            Text(
                text = "Rural Panchayat Homestays",
                style = YatriTypographyTokens.HeadlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "90% direct to host • 5% to Gram Panchayat village development",
                style = YatriTypographyTokens.BodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(SampleHomestays, key = { it.id }) { stay ->
            YatriCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(YatriRadius.Card))
                    ) {
                        AsyncImage(
                            model = stay.images.firstOrNull(),
                            contentDescription = stay.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                        Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                            YatriStatusPill(
                                label = "PANCHAYAT VERIFIED",
                                type = StatusPillType.VERIFIED
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(YatriSpacing.m))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stay.title,
                            style = YatriTypographyTokens.HeadlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "★ ${stay.rating}",
                            style = YatriTypographyTokens.LabelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hosted by ${stay.host.name} • ${stay.address}",
                        style = YatriTypographyTokens.LabelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stay.tagline,
                        style = YatriTypographyTokens.BodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(YatriSpacing.m))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "₹${stay.pricePerNightInr}",
                                style = YatriTypographyTokens.MetricScore.copy(fontSize = 22.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "per night (incl. taxes)",
                                style = YatriTypographyTokens.LabelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        YatriButton(
                            onClick = { onReserveHomestay(stay.id) },
                            variant = YatriButtonVariant.PRIMARY
                        ) {
                            Text("Reserve Stay")
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
