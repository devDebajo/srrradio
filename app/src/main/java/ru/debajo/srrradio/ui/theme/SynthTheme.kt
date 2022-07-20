package ru.debajo.srrradio.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import ru.debajo.srrradio.R

internal val SynthTheme: AppTheme = AppTheme(
    code = "SynthTheme",
    nameRes = R.string.theme_synthwave,
    colors = { SynthColors }
)

private val SynthColors: ColorScheme = ColorScheme(
    primary = Color(0xFFf8acfb),
    onPrimary = Color(0xFF51145a),
    primaryContainer = Color(0xFF6b2e72),
    onPrimaryContainer = Color(0xFFffd5ff),
    secondary = Color(0xFFd8bfd5),
    onSecondary = Color(0xFF3b2b3b),
    secondaryContainer = Color(0xFF534152),
    onSecondaryContainer = Color(0xFFf5dbf1),
    tertiary = Color(0xFFf5b8ad),
    onTertiary = Color(0xFF4c251e),
    tertiaryContainer = Color(0xFF673b33),
    onTertiaryContainer = Color(0xFFffdad2),
    error = Color(0xFFffb4a9),
    errorContainer = Color(0xFF930006),
    onError = Color(0xFF680003),
    onErrorContainer = Color(0xFFffdad4),
    background = Color(0xFF1e1a1d),
    onBackground = Color(0xFFe9e0e4),
    surface = Color(0xFF1e1a1d),
    onSurface = Color(0xFFe9e0e4),
    surfaceVariant = Color(0xFF4d444c),
    onSurfaceVariant = Color(0xFFd0c3cc),
    outline = Color(0xFF998e96),
    inverseOnSurface = Color(0xFF1e1a1d),
    inverseSurface = Color(0xFFe9e0e4),
    inversePrimary = Color(0xFF86468c),
    surfaceTint = Color(0xFFf8acfb),
)
