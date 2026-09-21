package com.yatrisetu.app.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = YatriColors.BrandAmberPrimary,
    onPrimary = YatriColors.LightSurface,
    primaryContainer = YatriColors.BrandAmberLight,
    onPrimaryContainer = YatriColors.LightTextPrimary,
    secondary = YatriColors.BrandAmberSecondary,
    onSecondary = YatriColors.LightSurface,
    background = YatriColors.LightBackground,
    onBackground = YatriColors.LightTextPrimary,
    surface = YatriColors.LightSurface,
    onSurface = YatriColors.LightTextPrimary,
    surfaceVariant = YatriColors.LightSurfaceSubtle,
    onSurfaceVariant = YatriColors.LightTextSecondary,
    outline = YatriColors.LightBorder,
    error = YatriColors.CrowdVeryHigh,
    onError = YatriColors.LightSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = YatriColors.BrandAmberLight,
    onPrimary = YatriColors.DarkBackground,
    primaryContainer = YatriColors.BrandAmberDark,
    onPrimaryContainer = YatriColors.DarkTextPrimary,
    secondary = YatriColors.BrandAmberSecondary,
    onSecondary = YatriColors.DarkBackground,
    background = YatriColors.DarkBackground,
    onBackground = YatriColors.DarkTextPrimary,
    surface = YatriColors.DarkSurface,
    onSurface = YatriColors.DarkTextPrimary,
    surfaceVariant = YatriColors.DarkSurfaceSubtle,
    onSurfaceVariant = YatriColors.DarkTextSecondary,
    outline = YatriColors.DarkBorder,
    error = YatriColors.CrowdVeryHighLight,
    onError = YatriColors.DarkBackground
)

@Composable
fun YatriSetuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = YatriTypography,
        shapes = YatriShapes,
        content = content
    )
}
