package ru.debajo.srrradio.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import ru.debajo.srrradio.R
import ru.debajo.srrradio.icon.AppIcon

internal val MintTheme: AppTheme = AppTheme(
    code = "MintTheme",
    nameRes = R.string.theme_mint,
    colors = { MintColors },
    icon = AppIcon.MINT,
)

private val MintColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF5CDBBE),
    onPrimary = Color(0xFF00382D),
    primaryContainer = Color(0xFF005142),
    onPrimaryContainer = Color(0xFF7BF8D9),
    secondary = Color(0xFFB1CCC3),
    onSecondary = Color(0xFF1D352E),
    secondaryContainer = Color(0xFF334B44),
    onSecondaryContainer = Color(0xFFCDE9DF),
    tertiary = Color(0xFFAACBE3),
    onTertiary = Color(0xFF103447),
    tertiaryContainer = Color(0xFF294A5E),
    onTertiaryContainer = Color(0xFFC6E7FF),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF191C1B),
    onBackground = Color(0xFFE1E3E0),
    surface = Color(0xFF191C1B),
    onSurface = Color(0xFFE1E3E0),
    surfaceVariant = Color(0xFF3F4945),
    onSurfaceVariant = Color(0xFFBFC9C4),
    outline = Color(0xFF89938F),
    inverseOnSurface = Color(0xFF191C1B),
    inverseSurface = Color(0xFFE1E3E0),
    inversePrimary = Color(0xFF006B59),
    surfaceTint = Color(0xFF5CDBBE),
)
