package ru.debajo.srrradio.ui.host.main.settings.model

sealed interface SettingsNews {
    class OpenUrl(val url: String) : SettingsNews
}
