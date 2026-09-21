package com.yatrisetu.app.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Yatri Setu Typography Abstraction
 * Sans font family: Plus Jakarta Sans (fallback: FontFamily.SansSerif)
 * Editorial font family: Cormorant Garamond (fallback: FontFamily.Serif)
 */
val YatriSansFontFamily: FontFamily = FontFamily.SansSerif
val YatriEditorialFontFamily: FontFamily = FontFamily.Serif

/**
 * Custom typography tokens tailored for mobile device legibility and accessibility.
 */
object YatriTypographyTokens {
    // Large display brand headlines
    val DisplayLarge = TextStyle(
        fontFamily = YatriSansFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp
    )

    // Editorial destination title (Cormorant Garamond italic serif style)
    val EditorialDestinationTitle = TextStyle(
        fontFamily = YatriEditorialFontFamily,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    )

    val EditorialTitle = EditorialDestinationTitle

    val HeadlineLarge = TextStyle(
        fontFamily = YatriSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.25).sp
    )

    val HeadlineMedium = TextStyle(
        fontFamily = YatriSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )

    val TitleLarge = TextStyle(
        fontFamily = YatriSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    )

    val TitleMedium = TextStyle(
        fontFamily = YatriSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    )

    val BodyLarge = TextStyle(
        fontFamily = YatriSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    )

    val BodyMedium = TextStyle(
        fontFamily = YatriSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    )

    val LabelLarge = TextStyle(
        fontFamily = YatriSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )

    val LabelSmall = TextStyle(
        fontFamily = YatriSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )

    // Specialized Metric & Gauge Numbers
    val MetricScore = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp
    )

    val MetricScoreLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Black,
        fontSize = 44.sp,
        lineHeight = 48.sp,
        letterSpacing = (-1.0).sp
    )
}

/**
 * Standard Material 3 Typography mapping
 */
val YatriTypography = Typography(
    displayLarge = YatriTypographyTokens.DisplayLarge,
    headlineLarge = YatriTypographyTokens.HeadlineLarge,
    headlineMedium = YatriTypographyTokens.HeadlineMedium,
    titleLarge = YatriTypographyTokens.TitleLarge,
    titleMedium = YatriTypographyTokens.TitleMedium,
    bodyLarge = YatriTypographyTokens.BodyLarge,
    bodyMedium = YatriTypographyTokens.BodyMedium,
    labelLarge = YatriTypographyTokens.LabelLarge,
    labelSmall = YatriTypographyTokens.LabelSmall
)
