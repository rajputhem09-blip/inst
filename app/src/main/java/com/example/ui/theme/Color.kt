package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Lyko Obsidian Brand Palette
val LykoVioletPrimary = Color(0xFF8B5CF6)
val LykoVioletDark = Color(0xFF6D28D9)
val LykoMagentaSecondary = Color(0xFFEC4899)
val LykoCyanTertiary = Color(0xFF06B6D4)
val LykoAmber = Color(0xFFF59E0B)
val LykoEmerald = Color(0xFF10B981)

val LykoDarkBackground = Color(0xFF0D0B14)
val LykoDarkSurface = Color(0xFF161224)
val LykoDarkSurfaceElevated = Color(0xFF1F1933)
val LykoDarkSurfaceContainer = Color(0xFF282042)
val LykoDarkOutline = Color(0xFF3B325A)
val LykoDarkOutlineSubtle = Color(0xFF251E3B)

val LykoLightBackground = Color(0xFFF8FAFC)
val LykoLightSurface = Color(0xFFFFFFFF)
val LykoLightSurfaceElevated = Color(0xFFF1F5F9)
val LykoLightSurfaceContainer = Color(0xFFE2E8F0)
val LykoLightOutline = Color(0xFFCBD5E1)
val LykoLightText = Color(0xFF0F172A)

// Custom Gradients
val LykoGradientBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF8B5CF6),
        Color(0xFFEC4899),
        Color(0xFF06B6D4)
    )
)

val LykoStoryGradient = Brush.sweepGradient(
    colors = listOf(
        Color(0xFF8B5CF6),
        Color(0xFFEC4899),
        Color(0xFFF59E0B),
        Color(0xFF06B6D4),
        Color(0xFF8B5CF6)
    )
)

val LykoLiveGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFEF4444),
        Color(0xFFF43F5E),
        Color(0xFFFB7185)
    )
)

val LykoObsidianGlow = Brush.verticalGradient(
    colors = listOf(
        Color(0x338B5CF6),
        Color(0x000D0B14)
    )
)
