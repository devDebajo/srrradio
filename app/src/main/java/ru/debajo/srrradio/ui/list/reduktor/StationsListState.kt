package ru.debajo.srrradio.ui.list.reduktor

import androidx.compose.runtime.Immutable
import ru.debajo.srrradio.RadioPlayer
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.UiStationPlayingState

@Immutable
sealed interface StationsListState {

    object Empty : StationsListState

    data class Loading(
        val playerState: RadioPlayer.State,
    ) : StationsListState

    data class Data(
        val searchQuery: String = "",
        val title: String? = null,
        val playerState: RadioPlayer.State = RadioPlayer.State.None,
        val stations: List<UiStation> = emptyList(),
    ) : StationsListState {

        fun stationPlayingState(index: Int): UiStationPlayingState {
            val station = stations.getOrNull(index) ?: return UiStationPlayingState.NONE
            return when (playerState) {
                is RadioPlayer.State.None -> UiStationPlayingState.NONE
                is RadioPlayer.State.HasStation -> {
                    if (station.id == playerState.station.id) {
                        when (playerState.playbackState) {
                            RadioPlayer.PlaybackState.PAUSED -> UiStationPlayingState.NONE
                            RadioPlayer.PlaybackState.BUFFERING -> UiStationPlayingState.BUFFERING
                            RadioPlayer.PlaybackState.PLAYING -> UiStationPlayingState.PLAYING
                        }
                    } else {
                        UiStationPlayingState.NONE
                    }
                }
            }
        }

        val currentStationIndex: Int
            get() {
                return when (playerState) {
                    is RadioPlayer.State.None -> -1
                    is RadioPlayer.State.HasStation -> stations.indexOfFirst { it.id == playerState.station.id }
                }
            }
    }
}
