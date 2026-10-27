package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LykoVioletPrimary,
    onPrimary = Color.White,
    primaryContainer = LykoVioletDark,
    onPrimaryContainer = Color(0xFFE9D5FF),
    secondary = LykoMagentaSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF831843),
    onSecondaryContainer = Color(0xFFFCE7F3),
    tertiary = LykoCyanTertiary,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF164E63),
    onTertiaryContainer = Color(0xFFCFFAFE),
    background = LykoDarkBackground,
    onBackground = Color(0xFFF8FAFC),
    surface = LykoDarkSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = LykoDarkSurfaceElevated,
    onSurfaceVariant = Color(0xFFCBD5E1),
    surfaceContainer = LykoDarkSurfaceContainer,
    outline = LykoDarkOutline,
    outlineVariant = LykoDarkOutlineSubtle
)

private val LightColorScheme = lightColorScheme(
    primary = LykoVioletPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = LykoVioletDark,
    secondary = LykoMagentaSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFCE7F3),
    onSecondaryContainer = Color(0xFF831843),
    tertiary = LykoCyanTertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCFFAFE),
    onTertiaryContainer = Color(0xFF164E63),
    background = LykoLightBackground,
    onBackground = LykoLightText,
    surface = LykoLightSurface,
    onSurface = LykoLightText,
    surfaceVariant = LykoLightSurfaceElevated,
    onSurfaceVariant = Color(0xFF475569),
    surfaceContainer = LykoLightSurfaceContainer,
    outline = LykoLightOutline,
    outlineVariant = Color(0xFFE2E8F0)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to obsidian dark for rich social media aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
