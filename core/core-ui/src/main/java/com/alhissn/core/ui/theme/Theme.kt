/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = NeonMint,
    tertiary = StatusError,
    background = BgDeep,
    surface = BgDark,
    onBackground = TextWhite,
    onSurface = TextWhite
)

private val LightColorScheme = lightColorScheme(
    primary = NeonCyan,
    secondary = NeonMint,
    tertiary = StatusError,
    background = BgLight,
    surface = SurfaceLight,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun AlHissNTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !darkTheme

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }

    val customColors = if (darkTheme) {
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
    } else {
        CustomColors(
            bg = BgLight,
            surface = SurfaceLight,
            border = BorderLight,
            text = TextDark,
            textMuted = TextMutedLight,
            statusOn = NeonCyan,
            statusOff = StatusError,
            accent = NeonCyan,
            success = StatusSuccess,
            warning = StatusWarning,
            surfaceBorder = BorderLight,
            glass = GlassLight,
            neonMint = NeonMint,
            neonCyan = NeonCyan
        )
    }

    CompositionLocalProvider(
        LocalCustomColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}


