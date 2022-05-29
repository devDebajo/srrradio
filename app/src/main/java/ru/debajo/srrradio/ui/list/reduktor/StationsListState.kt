package ru.debajo.srrradio.ui.list.reduktor

import androidx.compose.runtime.Immutable
import ru.debajo.srrradio.RadioPlayer
import ru.debajo.srrradio.ui.model.UiStation

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
        val currentStationIndex: Int
            get() {
                return when (playerState) {
                    is RadioPlayer.State.None -> -1
                    is RadioPlayer.State.HasStation -> stations.indexOfFirst { it.id == playerState.station.id }
                }
            }
    }
}
