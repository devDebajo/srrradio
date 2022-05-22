package ru.debajo.srrradio.ui.list.reduktor

sealed interface StationsListEvent {
    object Start : StationsListEvent
    class OnSearchQueryChanged(val  query: String) : StationsListEvent
}
