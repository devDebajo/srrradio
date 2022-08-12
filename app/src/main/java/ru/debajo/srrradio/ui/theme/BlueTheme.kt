package ru.debajo.srrradio.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import ru.debajo.srrradio.R
import ru.debajo.srrradio.icon.AppIcon

internal val BlueTheme: AppTheme = AppTheme(
    code = "BlueTheme",
    nameRes = R.string.theme_blue,
    colors = { BlueColors },
    icon = AppIcon.WAVE,
)

private val BlueColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF60D4FE),
    onPrimary = Color(0xFF003545),
    primaryContainer = Color(0xFF004D62),
    onPrimaryContainer = Color(0xFFBAEAFF),
    secondary = Color(0xFFB3CAD5),
    onSecondary = Color(0xFF1E333C),
    secondaryContainer = Color(0xFF354A53),
    onSecondaryContainer = Color(0xFFCFE6F1),
    tertiary = Color(0xFFC4C3EB),
    onTertiary = Color(0xFF2D2D4D),
    tertiaryContainer = Color(0xFF444465),
    onTertiaryContainer = Color(0xFFE2DFFF),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF191C1E),
    onBackground = Color(0xFFE1E3E4),
    surface = Color(0xFF191C1E),
    onSurface = Color(0xFFE1E3E4),
    surfaceVariant = Color(0xFF40484C),
    onSurfaceVariant = Color(0xFFC0C8CC),
    outline = Color(0xFF8A9296),
    inverseOnSurface = Color(0xFF191C1E),
    inverseSurface = Color(0xFFE1E3E4),
    inversePrimary = Color(0xFF006782),
    surfaceTint = Color(0xFF60D4FE),
)
