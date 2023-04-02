package ru.debajo.srrradio.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

internal class AppTheme(
    val code: String,
    val nameRes: Int,
    val colors: @Composable () -> ColorScheme,
)
