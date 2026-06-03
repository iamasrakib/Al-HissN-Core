/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.core.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Premium Cybersecurity Theme
val BgDeep = Color(0xFF050510)
val BgDark = Color(0xFF0A0A1A)
val NeonCyan = Color(0xFF00E5FF)
val NeonMint = Color(0xFF00FF9D)
val NeonBlue = Color(0xFF2962FF)
val GlassDark = Color(0x20FFFFFF)
val TextWhite = Color(0xFFFFFFFF)
val TextMuted = Color(0xFFA0A0B0)
val ShieldGlow = Color(0xFF00E5FF)
val ProgressTrack = Color(0xFF1A1A2A)
val BorderGlass = Color(0x30FFFFFF)

// Light Theme Colors
val BgLight = Color(0xFFF0F2F5)
val SurfaceLight = Color(0xFFFFFFFF)
val TextDark = Color(0xFF121212)
val TextMutedLight = Color(0xFF5F6368)
val BorderLight = Color(0xFFE0E0E0)
val GlassLight = Color(0x20000000)

// Status Colors
val StatusSuccess = NeonMint
val StatusWarning = Color(0xFFFFB300)
val StatusError = Color(0xFFFF5252)

// Code crafted by iamasrakib
class CustomColors(
    val bg: Color,
    val surface: Color,
    val border: Color,
    val text: Color,
    val textMuted: Color,
    val statusOn: Color,
    val statusOff: Color,
    val accent: Color = NeonCyan,
    val success: Color = StatusSuccess,
    val warning: Color = StatusWarning,
    val surfaceBorder: Color = BorderGlass,
    val glass: Color = GlassDark,
    val neonMint: Color = NeonMint,
    val neonCyan: Color = NeonCyan
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        bg = BgDeep,
        surface = BgDark,
        border = BorderGlass,
        text = TextWhite,
        textMuted = TextMuted,
        statusOn = NeonCyan,
        statusOff = StatusError,
        accent = NeonCyan,
        success = StatusSuccess,
        warning = StatusWarning,
        surfaceBorder = BorderGlass,
        glass = GlassDark,
        neonMint = NeonMint,
        neonCyan = NeonCyan
    )
}


