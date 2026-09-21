package com.yatrisetu.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Component Corner Radii
 */
object YatriRadius {
    val Card = 16.dp
    val CardLarge = 20.dp
    val Button = 24.dp
    val Pill = 50.dp
    val Sheet = 24.dp
    val Badge = 8.dp
}

/**
 * Spacing Tokens (Himalayan grid)
 */
object YatriSpacing {
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val s: Dp = 8.dp
    val m: Dp = 12.dp
    val l: Dp = 16.dp
    val xl: Dp = 20.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp
}

/**
 * Subtle Elevation Tokens for restrained shadows
 */
object YatriElevation {
    val Flat: Dp = 0.dp
    val Subtle: Dp = 2.dp
    val Card: Dp = 4.dp
    val Elevated: Dp = 8.dp
    val Floating: Dp = 12.dp
}

val YatriShapes = Shapes(
    small = RoundedCornerShape(YatriRadius.Badge),
    medium = RoundedCornerShape(YatriRadius.Card),
    large = RoundedCornerShape(YatriRadius.CardLarge),
    extraLarge = RoundedCornerShape(YatriRadius.Pill)
)
