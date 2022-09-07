package ru.debajo.srrradio.ui.host.main.settings.model

import org.joda.time.DateTime

internal data class SettingsState(
    val themes: List<SettingsTheme> = emptyList(),
    val loadingM3u: Boolean = false,
    val autoSendErrors: Boolean = false,
    val dynamicIcon: Boolean = false,
    val authStatus: SettingsAuthStatus = SettingsAuthStatus.NOT_SUPPORTED,
    val synchronization: Boolean = false,
    val lastSyncDate: DateTime? = null,
)
