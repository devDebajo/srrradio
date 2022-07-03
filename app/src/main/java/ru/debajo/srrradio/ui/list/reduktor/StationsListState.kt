package ru.debajo.srrradio.ui.list.reduktor

import androidx.compose.runtime.Immutable
import ru.debajo.srrradio.model.MediaState
import ru.debajo.srrradio.model.asLoaded
import ru.debajo.srrradio.ui.model.UiPlaylist
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.UiStationPlayingState

@Immutable
sealed interface StationsListState {

    @Immutable
    object Empty : StationsListState

    @Immutable
    data class Loading(val mediaState: MediaState) : StationsListState

    @Immutable
    data class Data(
        val searchQuery: String = "",

        val title: String? = null,

        // local playlist, may be not in MediaState
        val playlist: UiPlaylist? = null,

        val mediaState: MediaState = MediaState.None,
    ) : StationsListState {

        private val loadedMediaState: MediaState.Loaded? = mediaState.asLoaded

        val stations: List<UiStation> = playlist?.stations.orEmpty()

        fun stationPlayingState(index: Int): UiStationPlayingState {
            val playlist = playlist ?: return UiStationPlayingState.NONE
            val station = playlist.stations.getOrNull(index) ?: return UiStationPlayingState.NONE

            val mediaStationInfo = loadedMediaState?.mediaStationInfo
            if (station.id != mediaStationInfo?.currentStationId) {
                return UiStationPlayingState.NONE
            }
            return mediaStationInfo.playingState
        }
    }
}
