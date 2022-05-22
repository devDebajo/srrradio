package ru.debajo.srrradio.ui.list.reduktor

import ru.debajo.srrradio.RadioPlayer
import ru.debajo.srrradio.common.presentation.Akt
import ru.debajo.srrradio.common.presentation.CommandResult
import ru.debajo.srrradio.common.presentation.Reduktor
import ru.debajo.srrradio.ui.list.reduktor.processor.PlayerStateListenerCommandProcessor
import ru.debajo.srrradio.ui.list.reduktor.processor.SearchStationsCommandProcessor
import ru.debajo.srrradio.ui.model.UiStationPlayingState

class StationsListCommandResultReduktor : Reduktor<StationsListState, CommandResult, StationsListNews> {
    override fun invoke(state: StationsListState, event: CommandResult): Akt<StationsListState, StationsListNews> {
        return when (event) {
            is SearchStationsCommandProcessor.SearchResult -> reduceSearchResult(state, event)
            is PlayerStateListenerCommandProcessor.OnNewPlayerState -> reduceOnNewPlayerState(state, event)
            else -> Akt()
        }
    }

    private fun reduceSearchResult(
        state: StationsListState,
        event: SearchStationsCommandProcessor.SearchResult
    ): Akt<StationsListState, StationsListNews> {
        return when (state) {
            is StationsListState.Empty,
            is StationsListState.Loading -> Akt()
            is StationsListState.Data -> Akt(state = state.copy(stations = event.stations))
        }
    }

    private fun reduceOnNewPlayerState(
        state: StationsListState,
        event: PlayerStateListenerCommandProcessor.OnNewPlayerState
    ): Akt<StationsListState, StationsListNews> {
        return when (state) {
            is StationsListState.Empty -> Akt()
            is StationsListState.Loading -> Akt(state = state.copy(playerState = event.state))
            is StationsListState.Data -> {
                val newStationList = when (val playerState = event.state) {
                    is RadioPlayer.State.None -> state.stations.map { it.copy(playingState = UiStationPlayingState.NONE) }
                    is RadioPlayer.State.HasStation -> {
                        state.stations.map { station ->
                            if (station.id == playerState.station.id) {
                                station.copy(
                                    playingState = when {
                                        playerState.buffering -> UiStationPlayingState.BUFFERING
                                        playerState.playing -> UiStationPlayingState.PLAYING
                                        else -> UiStationPlayingState.NONE
                                    }
                                )
                            } else {
                                station.copy(playingState = UiStationPlayingState.NONE)
                            }
                        }
                    }
                }
                Akt(state = state.copy(stations = newStationList))
            }
        }
    }
}
