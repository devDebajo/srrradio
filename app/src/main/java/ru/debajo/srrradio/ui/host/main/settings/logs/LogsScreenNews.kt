package ru.debajo.srrradio.ui.host.main.settings.logs

sealed interface LogsScreenNews {
    class OpenMailApp(
        val path: String
    ) : LogsScreenNews
}
