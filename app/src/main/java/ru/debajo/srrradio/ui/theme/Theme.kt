package ru.debajo.srrradio.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SynthWaveColors: ColorScheme = darkColorScheme(
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
)

@Composable
fun SrrradioTheme(
    colors: ColorScheme = SynthWaveColors,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}

// blue

//val md_theme_dark_surfaceTint = Color(0xFFD0BCFF)
//val md_theme_dark_onErrorContainer = Color(0xFFF2B8B5)
//val md_theme_dark_onError = Color(0xFF601410)
//val md_theme_dark_errorContainer = Color(0xFF8C1D18)
//val md_theme_dark_onTertiaryContainer = Color(0xFFFFD8E4)
//val md_theme_dark_onTertiary = Color(0xFF492532)
//val md_theme_dark_tertiaryContainer = Color(0xFF633B48)
//val md_theme_dark_tertiary = Color(0xFFEFB8C8)
//val md_theme_dark_error = Color(0xFFF2B8B5)
//val md_theme_dark_outline = Color(0xFF938F99)
//val md_theme_dark_onBackground = Color(0xFFE6E1E5)
//val md_theme_dark_background = Color(0xFF1C1B1F)
//val md_theme_dark_inverseOnSurface = Color(0xFF313033)
//val md_theme_dark_inverseSurface = Color(0xFFE6E1E5)
//val md_theme_dark_onSurfaceVariant = Color(0xFFCAC4D0)
//val md_theme_dark_onSurface = Color(0xFFE6E1E5)
//val md_theme_dark_surfaceVariant = Color(0xFF49454F)
//val md_theme_dark_surface = Color(0xFF1C1B1F)
//val md_theme_dark_onSecondaryContainer = Color(0xFFE8DEF8)
//val md_theme_dark_onSecondary = Color(0xFF332D41)
//val md_theme_dark_secondaryContainer = Color(0xFF4A4458)
//val md_theme_dark_secondary = Color(0xFFCCC2DC)
//val md_theme_dark_inversePrimary = Color(0xFF6750A4)
//val md_theme_dark_onPrimaryContainer = Color(0xFFEADDFF)
//val md_theme_dark_onPrimary = Color(0xFF381E72)
//val md_theme_dark_primaryContainer = Color(0xFF4F378B)
//val md_theme_dark_primary = Color(0xFFD0BCFF)

// green

//val md_theme_dark_primary = Color(0xFF5CDBBE)
//val md_theme_dark_onPrimary = Color(0xFF00382D)
//val md_theme_dark_primaryContainer = Color(0xFF005142)
//val md_theme_dark_onPrimaryContainer = Color(0xFF7BF8D9)
//val md_theme_dark_secondary = Color(0xFFB1CCC3)
//val md_theme_dark_onSecondary = Color(0xFF1D352E)
//val md_theme_dark_secondaryContainer = Color(0xFF334B44)
//val md_theme_dark_onSecondaryContainer = Color(0xFFCDE9DF)
//val md_theme_dark_tertiary = Color(0xFFAACBE3)
//val md_theme_dark_onTertiary = Color(0xFF103447)
//val md_theme_dark_tertiaryContainer = Color(0xFF294A5E)
//val md_theme_dark_onTertiaryContainer = Color(0xFFC6E7FF)
//val md_theme_dark_error = Color(0xFFFFB4AB)
//val md_theme_dark_errorContainer = Color(0xFF93000A)
//val md_theme_dark_onError = Color(0xFF690005)
//val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
//val md_theme_dark_background = Color(0xFF191C1B)
//val md_theme_dark_onBackground = Color(0xFFE1E3E0)
//val md_theme_dark_surface = Color(0xFF191C1B)
//val md_theme_dark_onSurface = Color(0xFFE1E3E0)
//val md_theme_dark_surfaceVariant = Color(0xFF3F4945)
//val md_theme_dark_onSurfaceVariant = Color(0xFFBFC9C4)
//val md_theme_dark_outline = Color(0xFF89938F)
//val md_theme_dark_inverseOnSurface = Color(0xFF191C1B)
//val md_theme_dark_inverseSurface = Color(0xFFE1E3E0)
//val md_theme_dark_inversePrimary = Color(0xFF006B59)
//val md_theme_dark_shadow = Color(0xFF000000)
//val md_theme_dark_surfaceTint = Color(0xFF5CDBBE)
//val md_theme_dark_surfaceTintColor = Color(0xFF5CDBBE)
//
//private val DarkColors = darkColorScheme(
//    surfaceTint = md_theme_dark_surfaceTint,
//    onErrorContainer = md_theme_dark_onErrorContainer,
//    onError = md_theme_dark_onError,
//    errorContainer = md_theme_dark_errorContainer,
//    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
//    onTertiary = md_theme_dark_onTertiary,
//    tertiaryContainer = md_theme_dark_tertiaryContainer,
//    tertiary = md_theme_dark_tertiary,
//    error = md_theme_dark_error,
//    outline = md_theme_dark_outline,
//    onBackground = md_theme_dark_onBackground,
//    background = md_theme_dark_background,
//    inverseOnSurface = md_theme_dark_inverseOnSurface,
//    inverseSurface = md_theme_dark_inverseSurface,
//    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
//    onSurface = md_theme_dark_onSurface,
//    surfaceVariant = md_theme_dark_surfaceVariant,
//    surface = md_theme_dark_surface,
//    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
//    onSecondary = md_theme_dark_onSecondary,
//    secondaryContainer = md_theme_dark_secondaryContainer,
//    secondary = md_theme_dark_secondary,
//    inversePrimary = md_theme_dark_inversePrimary,
//    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
//    onPrimary = md_theme_dark_onPrimary,
//    primaryContainer = md_theme_dark_primaryContainer,
//    primary = md_theme_dark_primary,
//)