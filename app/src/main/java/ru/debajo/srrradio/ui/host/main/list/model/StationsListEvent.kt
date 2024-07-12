package ru.debajo.srrradio.ui.host.main.list.model

import androidx.compose.ui.text.input.TextFieldValue
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.UiStationPlayingState

sealed interface StationsListEvent {
    data object Start : StationsListEvent
    class OnSearchQueryChanged(val query: TextFieldValue) : StationsListEvent
    data object ClearSearch : StationsListEvent
    class OnPlayPauseStation(val station: UiStation, val playingState: UiStationPlayingState) : StationsListEvent
    class ChangeFavorite(val station: UiStation, val favorite: Boolean) : StationsListEvent
    data object UpdateApp : StationsListEvent
}
