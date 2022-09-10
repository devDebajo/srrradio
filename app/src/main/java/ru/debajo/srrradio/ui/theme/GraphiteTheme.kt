package ru.debajo.srrradio.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import ru.debajo.srrradio.R
import ru.debajo.srrradio.icon.AppIcon

internal val GraphiteTheme: AppTheme = AppTheme(
    code = "GraphiteTheme",
    nameRes = R.string.theme_graphite,
    colors = { GraphiteColors },
    icon = AppIcon.GRAPHITE,
)

private val GraphiteColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF808AE3),
    onPrimary = Color(0xFF292E49),
    surface = Color(0xFF212121),
    secondary = Color(0xFFB2B9F3),
    onSecondary = Color(0xFF2D2D2D),
    primaryContainer = Color(0xFF4C528C),
)
