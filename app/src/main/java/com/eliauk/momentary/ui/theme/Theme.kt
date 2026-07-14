package com.eliauk.momentary.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Olive,
    onPrimary = PaperRaised,
    primaryContainer = SunSoft,
    onPrimaryContainer = Ink,
    secondary = Sage,
    onSecondary = Ink,
    secondaryContainer = MistBlue,
    onSecondaryContainer = Ink,
    tertiary = DustRose,
    background = Paper,
    onBackground = Ink,
    surface = PaperRaised,
    onSurface = Ink,
    surfaceVariant = ColorTokens.surfaceMuted,
    onSurfaceVariant = MutedInk,
    outline = PaperLine,
)

private val DarkColors = darkColorScheme(
    primary = Sun,
    onPrimary = Ink,
    primaryContainer = Olive,
    onPrimaryContainer = NightInk,
    secondary = OliveSoft,
    background = NightPaper,
    onBackground = NightInk,
    surface = NightRaised,
    onSurface = NightInk,
    surfaceVariant = ColorTokens.nightMuted,
    onSurfaceVariant = ColorTokens.nightSecondary,
    outline = ColorTokens.nightLine,
)

private object ColorTokens {
    val surfaceMuted = androidx.compose.ui.graphics.Color(0xFFF4F0E5)
    val nightMuted = androidx.compose.ui.graphics.Color(0xFF33352C)
    val nightSecondary = androidx.compose.ui.graphics.Color(0xFFC9C5B9)
    val nightLine = androidx.compose.ui.graphics.Color(0xFF4A4B40)
}

private val MomentaryShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(8.dp),
)

data class MomentarySpacing(
    val xs: androidx.compose.ui.unit.Dp = 4.dp,
    val sm: androidx.compose.ui.unit.Dp = 8.dp,
    val md: androidx.compose.ui.unit.Dp = 12.dp,
    val lg: androidx.compose.ui.unit.Dp = 16.dp,
    val xl: androidx.compose.ui.unit.Dp = 24.dp,
    val xxl: androidx.compose.ui.unit.Dp = 32.dp,
)

val LocalMomentarySpacing = staticCompositionLocalOf { MomentarySpacing() }

@Composable
fun MomentaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            window.navigationBarColor = colors.background.toArgb()
        }
    }
    CompositionLocalProvider(LocalMomentarySpacing provides MomentarySpacing()) {
        MaterialTheme(
            colorScheme = colors,
            typography = MomentaryTypography,
            shapes = MomentaryShapes,
            content = content,
        )
    }
}
