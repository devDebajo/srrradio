package ru.debajo.srrradio.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun SrrradioTheme(
    content: @Composable () -> Unit
) {
    val manager = SrrradioThemeManager.Local.current
    val theme by manager.currentTheme.collectAsState()
    MaterialTheme(
        colorScheme = theme.colors(),
        typography = AppTypography,
        content = content
    )
}
