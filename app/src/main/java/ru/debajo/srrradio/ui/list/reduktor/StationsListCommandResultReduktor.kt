package ru.debajo.srrradio.ui.list.reduktor

import ru.debajo.srrradio.common.presentation.Akt
import ru.debajo.srrradio.common.presentation.CommandResult
import ru.debajo.srrradio.common.presentation.Reduktor
import ru.debajo.srrradio.ui.list.reduktor.processor.SearchStationsCommandProcessor

class StationsListCommandResultReduktor : Reduktor<StationsListState, CommandResult, StationsListNews> {
    override fun invoke(state: StationsListState, event: CommandResult): Akt<StationsListState, StationsListNews> {
        return when (event) {
            is SearchStationsCommandProcessor.SearchResult -> reduceSearchResult(state, event)
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
}
