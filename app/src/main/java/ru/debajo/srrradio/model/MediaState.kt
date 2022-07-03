package ru.debajo.srrradio.model

import androidx.compose.runtime.Immutable
import ru.debajo.srrradio.ui.model.UiPlaylist
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.UiStationPlayingState

@Immutable
sealed interface MediaState {
    object None : MediaState

    object Loading : MediaState

    object Empty : MediaState

    data class Loaded(
        val playlist: UiPlaylist,
        val mediaStationInfo: MediaStationInfo?,
    ) : MediaState {

        val playing: Boolean
            get() = mediaStationInfo?.playingState == UiStationPlayingState.PLAYING

        val buffering: Boolean
            get() = mediaStationInfo?.playingState == UiStationPlayingState.BUFFERING

        val currentStation: UiStation?
            get() = playlist.stations.getOrNull(currentStationIndex)

        val currentStationIndex: Int
            get() = playlist.stations.indexOfFirst { it.id == currentStationId }

        val currentStationId: String?
            get() = mediaStationInfo?.currentStationId
    }
}

val MediaState.asLoaded: MediaState.Loaded?
    get() = this as? MediaState.Loaded
