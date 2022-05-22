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
        val stations: List<UiStation> = emptyList(),
    ) : StationsListState
}
