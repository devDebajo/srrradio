package ru.debajo.srrradio.ui.list.reduktor

import ru.debajo.reduktor.Akt
import ru.debajo.reduktor.CommandResult
import ru.debajo.reduktor.Reduktor
import ru.debajo.srrradio.RadioPlayer
import ru.debajo.srrradio.ui.list.reduktor.processor.PlayerStateListenerCommandProcessor
import ru.debajo.srrradio.ui.list.reduktor.processor.SearchStationsCommandProcessor
import ru.debajo.srrradio.ui.model.UiStation

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
            is StationsListState.Data -> Akt(
                state = state.copy(
                    stations = event.stations,
                )
            )
        }
    }

    private fun reduceOnNewPlayerState(
        state: StationsListState,
        event: PlayerStateListenerCommandProcessor.OnNewPlayerState
    ): Akt<StationsListState, StationsListNews> {
        return when (state) {
            is StationsListState.Empty -> Akt()
            is StationsListState.Loading -> Akt(state = state.copy(playerState = event.state))
            is StationsListState.Data -> Akt(state = state.copy(playerState = event.state))
        }
    }
}

fun StationsListState.Data.findNextStation(deltaIndex: Int): UiStation? {
    return stations.findNextStation(playerState = playerState, deltaIndex = deltaIndex)
}

fun List<UiStation>.findNextStation(playerState: RadioPlayer.State?, deltaIndex: Int): UiStation? {
    if (playerState !is RadioPlayer.State.HasStation) return null
    val playingIndex = indexOfFirst { it.id == playerState.station.id }
    if (playingIndex == -1) return null
    return getOrNull(playingIndex + deltaIndex)
}
