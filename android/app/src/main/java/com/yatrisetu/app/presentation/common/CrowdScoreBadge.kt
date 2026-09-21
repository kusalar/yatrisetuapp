package com.yatrisetu.app.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.yatrisetu.app.domain.model.CrowdLevel
import com.yatrisetu.app.presentation.theme.YatriColors
import com.yatrisetu.app.presentation.theme.YatriRadius
import com.yatrisetu.app.presentation.theme.YatriTypographyTokens

@Composable
fun CrowdScoreBadge(
    score: Int,
    level: CrowdLevel,
    modifier: Modifier = Modifier
) {
    val (textColor, bgColor, borderColor) = when (level) {
        CrowdLevel.LOW -> Triple(
            YatriColors.CrowdLow,
            YatriColors.CrowdLowBg,
            YatriColors.CrowdLow.copy(alpha = 0.35f)
        )
        CrowdLevel.MEDIUM -> Triple(
            YatriColors.CrowdMedium,
            YatriColors.CrowdMediumBg,
            YatriColors.CrowdMedium.copy(alpha = 0.35f)
        )
        CrowdLevel.HIGH -> Triple(
            YatriColors.CrowdHigh,
            YatriColors.CrowdHighBg,
            YatriColors.CrowdHigh.copy(alpha = 0.35f)
        )
        CrowdLevel.VERY_HIGH -> Triple(
            YatriColors.CrowdVeryHigh,
            YatriColors.CrowdVeryHighBg,
            YatriColors.CrowdVeryHigh.copy(alpha = 0.35f)
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .semantics {
                this.contentDescription = "Crowd score: $score out of 100, level: ${level.label}"
            }
            .clip(RoundedCornerShape(YatriRadius.Badge))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(YatriRadius.Badge))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$score",
            style = YatriTypographyTokens.LabelLarge,
            color = textColor
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "• ${level.label}",
            style = YatriTypographyTokens.LabelSmall,
            color = textColor
        )
    }
}
