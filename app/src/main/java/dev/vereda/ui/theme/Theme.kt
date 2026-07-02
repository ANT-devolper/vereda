package dev.vereda.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Vereda is dark-first with a fixed brand identity: no Material You / dynamic color, so the
// serene-blue + gold palette is consistent on every device.
private val VeredaColorScheme =
    darkColorScheme(
        primary = VeredaBlue,
        onPrimary = VeredaOnBlue,
        primaryContainer = VeredaBlue,
        onPrimaryContainer = VeredaOnBlue,
        secondary = VeredaGold,
        onSecondary = VeredaOnGold,
        tertiary = VeredaGold,
        onTertiary = VeredaOnGold,
        background = VeredaBackground,
        onBackground = VeredaOnBackground,
        surface = VeredaSurface,
        onSurface = VeredaOnBackground,
        surfaceVariant = VeredaSurfaceVariant,
        onSurfaceVariant = VeredaOnBackground,
        outline = VeredaOutline,
        outlineVariant = VeredaSurfaceVariant,
    )

@Composable
fun VeredaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VeredaColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
