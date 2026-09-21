package com.yatrisetu.app.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.yatrisetu.app.presentation.theme.YatriColors
import com.yatrisetu.app.presentation.theme.YatriRadius
import com.yatrisetu.app.presentation.theme.YatriTypographyTokens

enum class StatusPillType {
    CROWD_LOW,
    CROWD_MEDIUM,
    CROWD_MODERATE,
    CROWD_HIGH,
    CROWD_VERY_HIGH,
    VERIFIED,
    AVAILABLE,
    FULL,
    ROAD_OPEN,
    ROAD_CAUTION,
    ROAD_DISRUPTED,
    NEUTRAL
}

@Composable
fun YatriStatusPill(
    label: String,
    type: StatusPillType,
    modifier: Modifier = Modifier,
    contentDesc: String? = null
) {
    val (dotColor, textColor, bgColor, borderColor) = when (type) {
        StatusPillType.CROWD_LOW -> Tuple4(
            YatriColors.CrowdLow,
            YatriColors.CrowdLow,
            YatriColors.CrowdLowBg,
            YatriColors.CrowdLow.copy(alpha = 0.3f)
        )
        StatusPillType.CROWD_MEDIUM, StatusPillType.CROWD_MODERATE -> Tuple4(
            YatriColors.CrowdMedium,
            YatriColors.CrowdMedium,
            YatriColors.CrowdMediumBg,
            YatriColors.CrowdMedium.copy(alpha = 0.3f)
        )
        StatusPillType.CROWD_HIGH -> Tuple4(
            YatriColors.CrowdHigh,
            YatriColors.CrowdHigh,
            YatriColors.CrowdHighBg,
            YatriColors.CrowdHigh.copy(alpha = 0.3f)
        )
        StatusPillType.CROWD_VERY_HIGH -> Tuple4(
            YatriColors.CrowdVeryHigh,
            YatriColors.CrowdVeryHigh,
            YatriColors.CrowdVeryHighBg,
            YatriColors.CrowdVeryHigh.copy(alpha = 0.3f)
        )
        StatusPillType.VERIFIED -> Tuple4(
            YatriColors.BrandAmberPrimary,
            YatriColors.BrandAmberPrimary,
            YatriColors.PanchayatBadgeBg,
            YatriColors.BrandAmberPrimary.copy(alpha = 0.3f)
        )
        StatusPillType.AVAILABLE -> Tuple4(
            YatriColors.CrowdLow,
            YatriColors.CrowdLow,
            YatriColors.CrowdLowBg,
            YatriColors.CrowdLow.copy(alpha = 0.3f)
        )
        StatusPillType.FULL -> Tuple4(
            YatriColors.CrowdVeryHigh,
            YatriColors.CrowdVeryHigh,
            YatriColors.CrowdVeryHighBg,
            YatriColors.CrowdVeryHigh.copy(alpha = 0.3f)
        )
        StatusPillType.ROAD_OPEN -> Tuple4(
            YatriColors.CrowdLow,
            YatriColors.CrowdLow,
            YatriColors.CrowdLowBg,
            YatriColors.CrowdLow.copy(alpha = 0.3f)
        )
        StatusPillType.ROAD_CAUTION -> Tuple4(
            YatriColors.CrowdMedium,
            YatriColors.CrowdMedium,
            YatriColors.CrowdMediumBg,
            YatriColors.CrowdMedium.copy(alpha = 0.3f)
        )
        StatusPillType.ROAD_DISRUPTED -> Tuple4(
            YatriColors.CrowdVeryHigh,
            YatriColors.CrowdVeryHigh,
            YatriColors.CrowdVeryHighBg,
            YatriColors.CrowdVeryHigh.copy(alpha = 0.3f)
        )
        StatusPillType.NEUTRAL -> Tuple4(
            YatriColors.LightTextMuted,
            YatriColors.LightTextMuted,
            Color.Transparent,
            YatriColors.LightBorder
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .semantics {
                contentDescription?.let { this.contentDescription = it }
            }
            .clip(RoundedCornerShape(YatriRadius.Pill))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(YatriRadius.Pill))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = YatriTypographyTokens.LabelSmall,
            color = textColor
        )
    }
}

private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
