package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Cyan400,
    onPrimary = Navy950,
    primaryContainer = Navy800,
    onPrimaryContainer = Cyan400,
    secondary = Emerald500,
    onSecondary = Navy950,
    secondaryContainer = Navy800,
    onSecondaryContainer = Emerald500,
    tertiary = Amber500,
    onTertiary = Navy950,
    background = Navy950,
    onBackground = Slate100,
    surface = Navy900,
    onSurface = Slate100,
    surfaceVariant = Navy800,
    onSurfaceVariant = Slate300,
    error = Rose500,
    onError = White
)

private val LightColorScheme = lightColorScheme(
    primary = Blue600,
    onPrimary = White,
    primaryContainer = Slate100,
    onPrimaryContainer = Blue600,
    secondary = Emerald600,
    onSecondary = White,
    secondaryContainer = Slate100,
    onSecondaryContainer = Emerald600,
    tertiary = Amber500,
    onTertiary = White,
    background = Color(0xFFF8FAFC),
    onBackground = Navy900,
    surface = White,
    onSurface = Navy900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate500,
    error = Rose500,
    onError = White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek executive dark theme for financial dashboards
    dynamicColor: Boolean = false, // Keep consistent branding
    content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
  )
}
