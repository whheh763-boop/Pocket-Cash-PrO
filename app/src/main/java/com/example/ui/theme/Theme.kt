package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PremiumDarkColorScheme = darkColorScheme(
    background = PremiumBackground,
    onBackground = PremiumOnBackground,
    surface = PremiumSurface,
    onSurface = PremiumOnBackground,
    surfaceVariant = PremiumSurfaceVariant,
    onSurfaceVariant = PremiumOnSurfaceVariant,
    primary = PremiumPrimary,
    onPrimary = Color.White,
    primaryContainer = PremiumPrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = PremiumSecondary,
    onSecondary = Color.White,
    secondaryContainer = PremiumSecondary,
    onSecondaryContainer = Color.White,
    outline = PremiumOutline,
    outlineVariant = PremiumOutlineVariant
)

@Composable
fun MyApplicationTheme(
  // Force dark theme for the premium aesthetic
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
      colorScheme = PremiumDarkColorScheme,
      typography = Typography,
      content = content
  )
}
