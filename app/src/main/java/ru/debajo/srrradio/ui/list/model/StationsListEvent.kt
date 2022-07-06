package ru.debajo.srrradio.ui.list.model

import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.UiStationPlayingState

sealed interface StationsListEvent {
    object Start : StationsListEvent
    class OnSearchQueryChanged(val query: String) : StationsListEvent
    class OnPlayPauseStation(val station: UiStation, val playingState: UiStationPlayingState) : StationsListEvent
}
