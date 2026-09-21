package com.yatrisetu.app.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Yatri Setu Centralized Color Palette
 * Derived from Himalayan Alabaster, Mountain Obsidian, Heritage Amber, and Eco Emerald.
 */
object YatriColors {
    // Light Theme Surfaces & Text
    val LightBackground = Color(0xFFF6F4F0)      // Himalayan Alabaster Paper
    val LightSurface = Color(0xFFFFFFFF)         // Pure Snow
    val LightSurfaceSubtle = Color(0xFFEDE9E2)   // Soft Mountain Sand
    val LightTextPrimary = Color(0xFF1C1917)     // Deep Stone 950
    val LightTextSecondary = Color(0xFF57534E)   // Stone 600
    val LightTextMuted = Color(0xFF78716C)       // Stone 500
    val LightBorder = Color(0x1A1C1917)          // 10% Stone 950

    // Dark Theme Surfaces & Text
    val DarkBackground = Color(0xFF0C0F14)       // Deep Mountain Obsidian
    val DarkSurface = Color(0xFF131722)          // Subtle Navy Slate
    val DarkSurfaceSubtle = Color(0xFF191E2C)    // Darkened Ridge
    val DarkTextPrimary = Color(0xFFF5F5F4)      // Warm Stone 100
    val DarkTextSecondary = Color(0xFFA8A29E)    // Stone 400
    val DarkTextMuted = Color(0xFF78716C)        // Stone 500
    val DarkBorder = Color(0x1FFFFFFF)           // 12% White

    // Brand Accents
    val BrandAmber = Color(0xFFB45309)           // Standard Himalayan Amber
    val BrandAmberPrimary = Color(0xFFB45309)    // Rich Himalayan Amber
    val BrandAmberSecondary = Color(0xFFD97706)  // Warm Goldenrod
    val BrandAmberLight = Color(0xFFF59E0B)      // Sunlight Gold
    val BrandAmberDark = Color(0xFF92400E)       // Deep Resin

    // Semantic Crowd Management Levels
    val CrowdLow = Color(0xFF059669)             // Emerald 600 - Pristine tranquility
    val CrowdLowLight = Color(0xFF10B981)        // Emerald 500
    val CrowdLowBg = Color(0x1A059669)           // 10% Emerald Tint

    val CrowdMedium = Color(0xFFD97706)          // Warm Amber 600 - Balanced footfall
    val CrowdMediumLight = Color(0xFFF59E0B)     // Amber 500
    val CrowdMediumBg = Color(0x1AD97706)        // 10% Amber Tint

    val CrowdHigh = Color(0xFFEA580C)            // Orange 600 - Elevated density
    val CrowdHighLight = Color(0xFFFB923C)       // Orange 400
    val CrowdHighBg = Color(0x1AEA580C)          // 10% Orange Tint

    val CrowdVeryHigh = Color(0xFFDC2626)        // Crimson 600 - Critical congestion alert
    val CrowdVeryHighLight = Color(0xFFF87171)   // Rose 400
    val CrowdVeryHighBg = Color(0x1ADC2626)      // 10% Crimson Tint

    // Emergency SOS & Traveler Safety
    val SosRedPrimary = Color(0xFFDC2626)
    val SosRedDark = Color(0xFF991B1B)
    val SosRedGlow = Color(0x66DC2626)

    // Verification & Sustainability Badges
    val PanchayatBadge = Color(0xFFB45309)
    val PanchayatBadgeBg = Color(0x1AB45309)
    val GreenCreditBadge = Color(0xFF059669)
    val GreenCreditBadgeBg = Color(0x1A059669)
}
