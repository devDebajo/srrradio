package ru.debajo.srrradio.ui.host.main.playlist

import androidx.compose.runtime.Immutable
import ru.debajo.srrradio.ui.model.UiPlaylist
import ru.debajo.srrradio.ui.model.UiStationElement

@Immutable
sealed interface DefaultPlaylistState {
    val items: List<UiStationElement>

    data object Loading : DefaultPlaylistState {
        override val items: List<UiStationElement> = emptyList()
    }

    data class Loaded(
        override val items: List<UiStationElement> = emptyList(),
        val playlist: UiPlaylist? = null
    ) : DefaultPlaylistState
}
