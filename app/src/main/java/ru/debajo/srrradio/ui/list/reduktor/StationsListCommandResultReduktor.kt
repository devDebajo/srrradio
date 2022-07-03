package ru.debajo.srrradio.ui.list.reduktor

import ru.debajo.reduktor.Akt
import ru.debajo.reduktor.CommandResult
import ru.debajo.reduktor.Reduktor
import ru.debajo.srrradio.ui.list.reduktor.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.list.reduktor.processor.SearchStationsCommandProcessor
import ru.debajo.srrradio.ui.model.UiPlaylist
import java.util.*

class StationsListCommandResultReduktor : Reduktor<StationsListState, CommandResult, StationsListNews> {
    override fun invoke(state: StationsListState, event: CommandResult): Akt<StationsListState, StationsListNews> {
        return when (event) {
            is SearchStationsCommandProcessor.SearchResult -> reduceSearchResult(state, event)
            is MediaStateListenerCommandProcessor.OnNewMediaState -> reduceOnNewMediaState(state, event)
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
            is StationsListState.Data -> {
                Akt(
                    state = state.copy(
                        playlist = UiPlaylist(
                            id = UUID.randomUUID().toString(),
                            name = "No named playlist",
                            stations = event.stations,
                        )
                    )
                )
            }
        }
    }

    private fun reduceOnNewMediaState(
        state: StationsListState,
        event: MediaStateListenerCommandProcessor.OnNewMediaState,
    ): Akt<StationsListState, StationsListNews> {
        return when (state) {
            is StationsListState.Empty -> Akt()
            is StationsListState.Loading -> Akt(state = state.copy(mediaState = event.state))
            is StationsListState.Data -> Akt(state = state.copy(mediaState = event.state))
        }
    }
}
