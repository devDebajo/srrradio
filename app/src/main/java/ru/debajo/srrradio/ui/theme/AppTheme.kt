package ru.debajo.srrradio.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import ru.debajo.srrradio.icon.AppIcon

internal class AppTheme(
    val code: String,
    val nameRes: Int,
    val icon: AppIcon,
    val colors: @Composable () -> ColorScheme,
)
