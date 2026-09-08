cat << 'INNER_EOF' > app/src/main/java/com/example/ui/theme/Color.kt
package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Premium Deep Dark Theme Colors (Based on User's Screenshot)
val PremiumBackground = Color(0xFF0D0E15)
val PremiumSurface = Color(0xFF151624)
val PremiumSurfaceVariant = Color(0xFF1E1F30)
val PremiumOnBackground = Color(0xFFFFFFFF)
val PremiumOnSurfaceVariant = Color(0xFFA0ABC0)

val PremiumPrimary = Color(0xFF8250F4) // Purple/Blue gradient start
val PremiumPrimaryDark = Color(0xFF6C3CE0)
val PremiumSecondary = Color(0xFFFFC107) // Gold Coin

val PremiumOutline = Color(0xFF2A2B3C)
val PremiumOutlineVariant = Color(0xFF383A52)

// Specific Gradients
val GradientLogoStart = Color(0xFF9060FF)
val GradientLogoEnd = Color(0xFF5E42FA)

val GradientButtonStart = Color(0xFF8250F4)
val GradientButtonEnd = Color(0xFF9568FF)

// Icon Tints
val IconTintYellow = Color(0xFFFFD166)
val IconTintCyan = Color(0xFF00E5FF)
val IconTintGreen = Color(0xFF06D6A0)
val IconTintRed = Color(0xFFFF5A5F)
val IconTintBlue = Color(0xFF4361EE)
val IconTintPink = Color(0xFFF72585)
INNER_EOF

cat << 'INNER_EOF' > app/src/main/java/com/example/ui/theme/Theme.kt
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
INNER_EOF
