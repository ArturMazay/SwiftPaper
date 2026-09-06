package com.swiftpaper.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = TealDark,
    secondary = SlateMuted,
    onSecondary = Color.White,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = Color.White,
    onSurface = OnSurfaceLight,
    outline = OutlineSoft
)

private val DarkColors = darkColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = TealDark,
    onPrimaryContainer = Color.White,
    secondary = OutlineSoft,
    onSecondary = Slate,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = Color(0xFF1F2937),
    onSurface = OnSurfaceDark,
    outline = SlateMuted
)

@Composable
fun SwiftPaperTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
