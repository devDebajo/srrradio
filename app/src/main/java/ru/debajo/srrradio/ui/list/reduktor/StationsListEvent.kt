package ru.debajo.srrradio.ui.list.reduktor

import ru.debajo.srrradio.ui.model.UiStation

sealed interface StationsListEvent {
    object Start : StationsListEvent
    class OnSearchQueryChanged(val query: String) : StationsListEvent
    class OnPlayPauseClick(val station: UiStation) : StationsListEvent
    object OnPlayClick : StationsListEvent
    object OnPauseClick : StationsListEvent
    class ChangeStation(val station: UiStation) : StationsListEvent
}
