package ru.debajo.srrradio.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import ru.debajo.srrradio.R
import ru.debajo.srrradio.icon.AppIcon

internal val SandTheme: AppTheme = AppTheme(
    code = "SandTheme",
    nameRes = R.string.theme_sand,
    colors = { SandColors },
    icon = AppIcon.SAND,
)

private val SandColors: ColorScheme = darkColorScheme(
    primary = Color(0xFFE8C426),
    onPrimary = Color(0xFF3B2F00),
    primaryContainer = Color(0xFF554600),
    onPrimaryContainer = Color(0xFFFFE171),
    secondary = Color(0xFFD2C6A1),
    onSecondary = Color(0xFF373016),
    secondaryContainer = Color(0xFF4E462A),
    onSecondaryContainer = Color(0xFFEFE2BC),
    tertiary = Color(0xFFAAD0B1),
    onTertiary = Color(0xFF163722),
    tertiaryContainer = Color(0xFF2D4E37),
    onTertiaryContainer = Color(0xFFC6ECCC),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1D1B16),
    onBackground = Color(0xFFE8E2D9),
    surface = Color(0xFF1D1B16),
    onSurface = Color(0xFFE8E2D9),
    surfaceVariant = Color(0xFF4B4639),
    onSurfaceVariant = Color(0xFFCEC6B4),
    outline = Color(0xFF979080),
    inverseOnSurface = Color(0xFF1D1B16),
    inverseSurface = Color(0xFFE8E2D9),
    inversePrimary = Color(0xFF705D00),
    surfaceTint = Color(0xFFE8C426),
)
