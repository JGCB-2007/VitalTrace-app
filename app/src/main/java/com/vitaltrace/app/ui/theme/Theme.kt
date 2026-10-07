package com.vitaltrace.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private val DarkColorScheme = darkColorScheme(
    primary = VitalTraceDarkTeal,
    onPrimary = VitalTraceDarkNavy,
    primaryContainer = VitalTraceDarkNavyContainer,
    onPrimaryContainer = VitalTraceDarkText,
    secondary = VitalTraceDarkTeal,
    onSecondary = VitalTraceDarkNavy,
    secondaryContainer = VitalTraceDarkTealContainer,
    onSecondaryContainer = VitalTraceDarkText,
    tertiary = VitalTraceMint,
    onTertiary = VitalTraceDarkNavy,
    tertiaryContainer = VitalTraceDarkMintContainer,
    onTertiaryContainer = VitalTraceDarkText,
    background = VitalTraceDarkNavy,
    onBackground = VitalTraceDarkText,
    surface = VitalTraceDarkSurface,
    onSurface = VitalTraceDarkText,
    surfaceVariant = VitalTraceDarkSurfaceVariant,
    onSurfaceVariant = VitalTraceDarkSupporting,
    outline = VitalTraceDarkSupporting,
    outlineVariant = VitalTraceDarkOutlineVariant,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = VitalTraceDarkErrorContainer,
    onErrorContainer = VitalTraceDarkOnErrorContainer,
    surfaceTint = VitalTraceDarkTeal,
    inverseSurface = VitalTraceDarkText,
    inverseOnSurface = VitalTraceDarkNavy,
    inversePrimary = VitalTraceTeal,
    scrim = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = VitalTraceNavy,
    onPrimary = Color.White,
    primaryContainer = VitalTraceNavyContainer,
    onPrimaryContainer = VitalTraceNavy,
    secondary = VitalTraceTeal,
    onSecondary = Color.White,
    secondaryContainer = VitalTraceTealContainer,
    onSecondaryContainer = VitalTraceNavy,
    tertiary = VitalTraceMint,
    onTertiary = VitalTraceNavy,
    tertiaryContainer = VitalTraceMintContainer,
    onTertiaryContainer = VitalTraceNavy,
    background = VitalTraceWarmBackground,
    onBackground = VitalTraceText,
    surface = VitalTraceSurface,
    onSurface = VitalTraceText,
    surfaceVariant = VitalTraceSurfaceVariant,
    onSurfaceVariant = VitalTraceSupportingText,
    outline = VitalTraceOutline,
    outlineVariant = VitalTraceOutlineVariant,
    error = VitalTraceError
)

@Composable
fun VitalTraceTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    largeText: Boolean = false,
    content: @Composable () -> Unit
) {
    // The brand palette is intentionally stable. Keep the parameter for preview/source
    // compatibility while avoiding wallpaper colors that conflict with clinical states.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val density = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = density.density,
            fontScale = if (largeText) maxOf(density.fontScale, 1.15f) else density.fontScale
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
