package com.yatrisetu.app.presentation.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatrisetu.app.domain.model.CrowdLevel
import com.yatrisetu.app.presentation.theme.YatriColors
import com.yatrisetu.app.presentation.theme.YatriTypographyTokens

/**
 * CrowdGauge
 * High-precision circular gauge drawn via Compose Canvas.
 * Renders an animated active progress stroke, center score display with "/100",
 * and semantic color matching the 4 crowd tiers.
 */
@Composable
fun CrowdGauge(
    score: Int,
    level: CrowdLevel,
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    strokeWidth: Dp = 10.dp,
    showSubtext: Boolean = true
) {
    val clampedScore = score.coerceIn(0, 100)
    var animationTarget by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(clampedScore) {
        animationTarget = clampedScore.toFloat()
    }

    val animatedScore by animateFloatAsState(
        targetValue = animationTarget,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "CrowdGaugeAnimation"
    )

    val activeColor = when (level) {
        CrowdLevel.LOW -> YatriColors.CrowdLow
        CrowdLevel.MEDIUM -> YatriColors.CrowdMedium
        CrowdLevel.HIGH -> YatriColors.CrowdHigh
        CrowdLevel.VERY_HIGH -> YatriColors.CrowdVeryHigh
    }

    val trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.semantics {
            contentDescription = "Crowd Index Gauge: $score out of 100, classification: ${level.label} footfall."
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size)
        ) {
            Canvas(modifier = Modifier.size(size)) {
                val strokePx = strokeWidth.toPx()
                val radius = (size.toPx() - strokePx) / 2f
                val arcSweep = 270f
                val startAngle = 135f

                // Background Track
                drawArc(
                    color = trackColor,
                    startAngle = startAngle,
                    sweepAngle = arcSweep,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )

                // Active Progress Arc
                val progressSweep = (animatedScore / 100f) * arcSweep
                if (progressSweep > 0f) {
                    drawArc(
                        color = activeColor,
                        startAngle = startAngle,
                        sweepAngle = progressSweep,
                        useCenter = false,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )
                }
            }

            // Center Content
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${animatedScore.toInt()}",
                        style = YatriTypographyTokens.MetricScore,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "/100",
                        style = YatriTypographyTokens.LabelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "CROWD INDEX",
                    style = YatriTypographyTokens.LabelSmall.copy(letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (showSubtext) {
            Spacer(modifier = Modifier.height(10.dp))
            YatriStatusPill(
                label = "${level.label} FOOTFALL",
                type = when (level) {
                    CrowdLevel.LOW -> StatusPillType.CROWD_LOW
                    CrowdLevel.MEDIUM -> StatusPillType.CROWD_MEDIUM
                    CrowdLevel.HIGH -> StatusPillType.CROWD_HIGH
                    CrowdLevel.VERY_HIGH -> StatusPillType.CROWD_VERY_HIGH
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CrowdGaugePreview() {
    CrowdGauge(score = 88, level = CrowdLevel.VERY_HIGH)
}
