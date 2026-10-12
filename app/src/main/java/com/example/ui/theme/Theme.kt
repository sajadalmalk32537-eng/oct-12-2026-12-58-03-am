package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TamaneenaColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = RoyalDarkNavy,
    primaryContainer = GoldContainer,
    onPrimaryContainer = GoldLight,
    secondary = GoldLight,
    onSecondary = RoyalDarkNavy,
    secondaryContainer = NavySurfaceElevated,
    onSecondaryContainer = TextWhite,
    tertiary = EmeraldGreen,
    onTertiary = TextWhite,
    background = RoyalDarkNavy,
    onBackground = TextWhite,
    surface = DeepNavyCard,
    onSurface = TextWhite,
    surfaceVariant = NavySurface,
    onSurfaceVariant = TextGrayLight,
    outline = NavyStroke,
    outlineVariant = NavyStrokeLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Keep signature compatible
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TamaneenaColorScheme,
        typography = Typography,
        content = content
    )
}
