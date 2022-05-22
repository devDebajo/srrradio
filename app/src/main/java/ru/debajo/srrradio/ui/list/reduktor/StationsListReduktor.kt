package ru.debajo.srrradio.ui.list.reduktor

import ru.debajo.srrradio.RadioPlayer
import ru.debajo.srrradio.common.presentation.Akt
import ru.debajo.srrradio.common.presentation.Reduktor
import ru.debajo.srrradio.ui.list.reduktor.processor.PlayerStateListenerCommandProcessor
import ru.debajo.srrradio.ui.list.reduktor.processor.SearchStationsCommandProcessor
import ru.debajo.srrradio.ui.model.UiStationPlayingState

class StationsListReduktor(
    private val radioPlayer: RadioPlayer,
) : Reduktor<StationsListState, StationsListEvent, StationsListNews> {

    override fun invoke(state: StationsListState, event: StationsListEvent): Akt<StationsListState, StationsListNews> {
        return when (event) {
            is StationsListEvent.Start -> reduceStart(state)
            is StationsListEvent.OnSearchQueryChanged -> reduceOnSearchQueryChanged(state, event)
            is StationsListEvent.OnPlayPauseClick -> reduceOnPlayPauseClick(state, event)
            is StationsListEvent.ChangeStation -> reduceChangeStation(state, event)
        }
    }

    private fun reduceStart(state: StationsListState): Akt<StationsListState, StationsListNews> {
        return when (state) {
            is StationsListState.Loading,
            is StationsListState.Data -> Akt()
            is StationsListState.Empty -> Akt(
                state = StationsListState.Data(),
                commands = listOf(PlayerStateListenerCommandProcessor.ListenerCommand.Start)
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
        event: StationsListEvent.OnPlayPauseClick,
    ): Akt<StationsListState, StationsListNews> {
        return when (state) {
            is StationsListState.Empty,
            is StationsListState.Loading -> Akt()
            is StationsListState.Data -> {
                radioPlayer.changeStation(event.station, playWhenReady = event.station.playingState != UiStationPlayingState.PLAYING)
                Akt()
            }
        }
    }

    private fun reduceChangeStation(
        state: StationsListState,
        event: StationsListEvent.ChangeStation
    ): Akt<StationsListState, StationsListNews> {
        return when (state) {
            is StationsListState.Empty,
            is StationsListState.Loading -> Akt()
            is StationsListState.Data -> {
                radioPlayer.changeStation(event.station, playWhenReady = event.station.playingState != UiStationPlayingState.PLAYING)
                Akt(state = state.copy(playerState = null))
            }
        }
    }
}
