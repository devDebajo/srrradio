package ru.debajo.srrradio.ui.list.reduktor

import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.UiStationPlayingState

sealed interface StationsListEvent {
    object Start : StationsListEvent
    class OnSearchQueryChanged(val query: String) : StationsListEvent
    class OnPlayPauseClick(val station: UiStation, val playingState: UiStationPlayingState) : StationsListEvent
    object OnPlayClick : StationsListEvent
    object OnPauseClick : StationsListEvent
    class ChangeStation(val station: UiStation, val play: Boolean? = null) : StationsListEvent
}
