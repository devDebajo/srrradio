package ru.debajo.srrradio.ui.host.main.settings.model

internal data class SettingsState(
    val themes: List<SettingsTheme> = emptyList(),
    val loadingM3u: Boolean = false,
    val autoSendErrors: Boolean = false,
    val dynamicIcon: Boolean = false,
)