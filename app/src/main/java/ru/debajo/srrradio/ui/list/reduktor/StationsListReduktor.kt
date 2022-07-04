package ru.debajo.srrradio.ui.list.reduktor

import ru.debajo.reduktor.Akt
import ru.debajo.reduktor.Reduktor
import ru.debajo.srrradio.ui.list.reduktor.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.list.reduktor.processor.NewPlayCommandProcessor
import ru.debajo.srrradio.ui.list.reduktor.processor.SearchStationsCommandProcessor
import ru.debajo.srrradio.ui.model.UiStationPlayingState

class StationsListReduktor : Reduktor<StationsListState, StationsListEvent, StationsListNews> {

    override fun invoke(state: StationsListState, event: StationsListEvent): Akt<StationsListState, StationsListNews> {
        return when (event) {
            is StationsListEvent.Start -> reduceStart()
            is StationsListEvent.OnSearchQueryChanged -> reduceOnSearchQueryChanged(state, event)
            is StationsListEvent.OnPlayPauseStation -> reduceOnPlayPauseClick(state, event)
        }
    }

    private fun reduceStart(): Akt<StationsListState, StationsListNews> {
        return Akt(commands = listOf(MediaStateListenerCommandProcessor.ListenerCommand.Start))
    }

    private fun reduceOnSearchQueryChanged(
        state: StationsListState,
        event: StationsListEvent.OnSearchQueryChanged
    ): Akt<StationsListState, StationsListNews> {
        return Akt(
            state = state.copy(searchQuery = event.query),
            commands = listOf(SearchStationsCommandProcessor.SearchCommand(event.query))
        )
    }

    private fun reduceOnPlayPauseClick(
        state: StationsListState,
        event: StationsListEvent.OnPlayPauseStation,
    ): Akt<StationsListState, StationsListNews> {
        val playlist = state.playlist ?: return Akt()
        return Akt(
            commands = listOf(
                NewPlayCommandProcessor.NewPlay(
                    playlist = playlist,
                    stationId = event.station.id,
                    play = event.playingState != UiStationPlayingState.PLAYING,
                )
            )
        )
    }
}
