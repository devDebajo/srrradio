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
            is StationsListEvent.Start -> reduceStart(state)
            is StationsListEvent.OnSearchQueryChanged -> reduceOnSearchQueryChanged(state, event)
            is StationsListEvent.OnPlayPauseStation -> reduceOnPlayPauseClick(state, event)
        }
    }

    private fun reduceStart(state: StationsListState): Akt<StationsListState, StationsListNews> {
        return when (state) {
            is StationsListState.Loading,
            is StationsListState.Data -> Akt()
            is StationsListState.Empty -> Akt(
                state = StationsListState.Data(),
                commands = listOf(MediaStateListenerCommandProcessor.ListenerCommand.Start)
            )
        }
    }

    private fun reduceOnSearchQueryChanged(
        state: StationsListState,
        event: StationsListEvent.OnSearchQueryChanged
    ): Akt<StationsListState, StationsListNews> {
        return when (state) {
            is StationsListState.Empty,
            is StationsListState.Loading -> Akt()
            is StationsListState.Data -> {
                Akt(
                    state = state.copy(searchQuery = event.query),
                    commands = listOf(SearchStationsCommandProcessor.SearchCommand(event.query))
                )
            }
        }
    }

    private fun reduceOnPlayPauseClick(
        state: StationsListState,
        event: StationsListEvent.OnPlayPauseStation,
    ): Akt<StationsListState, StationsListNews> {
        return when (state) {
            is StationsListState.Empty,
            is StationsListState.Loading -> Akt()
            is StationsListState.Data -> {
                val playlist = state.playlist ?: return Akt()
                Akt(
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
    }
}
