package com.yatrisetu.app.presentation.itinerary

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yatrisetu.app.domain.model.ActivitySlot
import com.yatrisetu.app.domain.model.ItineraryDay
import com.yatrisetu.app.presentation.common.StatusPillType
import com.yatrisetu.app.presentation.common.YatriButton
import com.yatrisetu.app.presentation.common.YatriButtonVariant
import com.yatrisetu.app.presentation.common.YatriCard
import com.yatrisetu.app.presentation.common.YatriStatusPill
import com.yatrisetu.app.presentation.theme.YatriColors
import com.yatrisetu.app.presentation.theme.YatriSpacing
import com.yatrisetu.app.presentation.theme.YatriTypographyTokens

private val SampleDays = listOf(
    ItineraryDay(
        dayNumber = 1,
        theme = "Heritage Ridge & Orchid Sanctuaries",
        slots = listOf(
            ActivitySlot("Morning (08:30 - 11:30)", "Pineview Nursery Orchid Trail", "Walk through 400+ orchid species with local Sherpa host.", "Zero queue; off-peak visiting hours", 0),
            ActivitySlot("Afternoon (12:30 - 15:30)", "Zang Dhok Palri Phodang Monastery", "Ancient Tibetan Buddhist shrine atop Durpin Dara ridge.", "Quiet mountain hours", 0),
            ActivitySlot("Evening (16:30 - 19:00)", "Upper Cart Road Sunset Stroll", "Unobstructed vistas of Kangchenjunga and fireside ginger tea.", "Local community promenade", 0)
        )
    ),
    ItineraryDay(
        dayNumber = 2,
        theme = "Alpine Hamlets & Lepcha Village Life",
        slots = listOf(
            ActivitySlot("Morning (07:00 - 11:00)", "Deolo Hill Alpine Walk", "Highest point in Kalimpong with panoramic valley views.", "Visit before 10 AM to avoid tourist buses", 50),
            ActivitySlot("Afternoon (12:00 - 15:00)", "Artisanal Cheese & Craft Guild", "Taste indigenous Kalimpong cheese made by local cooperative.", "Exclusive small group entry", 150)
        )
    ),
    ItineraryDay(
        dayNumber = 3,
        theme = "Tea Terraces & Forest Canopies",
        slots = listOf(
            ActivitySlot("Morning (08:00 - 12:00)", "Forest Birding Trail & Teesta Lookouts", "Trek through pine woods and discover high altitude Himalayan avifauna.", "Guided eco-trail", 200)
        )
    )
)

@Composable
fun ItineraryScreen(
    destinationName: String = "Kalimpong",
    onNavigateBack: () -> Unit,
    onSelectHomestay: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDayIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Column(modifier = Modifier.padding(YatriSpacing.l)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$destinationName Smart Itinerary",
                    style = YatriTypographyTokens.HeadlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                YatriStatusPill(
                    label = "91% OVERCROWD AVOIDED",
                    type = StatusPillType.CROWD_LOW
                )
            }
            Spacer(modifier = Modifier.height(YatriSpacing.xs))
            Text(
                text = "Dynamic AI schedule balancing calm ridges and community impact",
                style = YatriTypographyTokens.BodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Day Tabs
        TabRow(
            selectedTabIndex = selectedDayIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = YatriColors.BrandAmberPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedDayIndex]),
                    color = YatriColors.BrandAmberPrimary
                )
            }
        ) {
            SampleDays.forEachIndexed { index, day ->
                Tab(
                    selected = selectedDayIndex == index,
                    onClick = { selectedDayIndex = index },
                    text = {
                        Text(
                            text = "Day ${day.dayNumber}",
                            style = YatriTypographyTokens.LabelLarge
                        )
                    }
                )
            }
        }

        // Slots
        val activeDay = SampleDays[selectedDayIndex]
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(YatriSpacing.l),
            verticalArrangement = Arrangement.spacedBy(YatriSpacing.m)
        ) {
            item {
                Text(
                    text = activeDay.theme,
                    style = YatriTypographyTokens.HeadlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(YatriSpacing.xs))
            }

            items(activeDay.slots) { slot ->
                YatriCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = slot.timeSlot,
                            style = YatriTypographyTokens.LabelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = slot.title,
                            style = YatriTypographyTokens.TitleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = slot.description,
                            style = YatriTypographyTokens.BodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        YatriStatusPill(
                            label = slot.crowdAvoidanceTip,
                            type = StatusPillType.CROWD_LOW
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(YatriSpacing.m))
                YatriButton(
                    onClick = onSelectHomestay,
                    variant = YatriButtonVariant.PRIMARY,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Verified Panchayat Homestay")
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
