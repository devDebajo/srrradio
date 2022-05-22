package ru.debajo.srrradio.ui.list.reduktor

import ru.debajo.srrradio.common.presentation.Akt
import ru.debajo.srrradio.common.presentation.Reduktor
import ru.debajo.srrradio.ui.list.reduktor.processor.SearchStationsCommandProcessor

class StationsListReduktor : Reduktor<StationsListState, StationsListEvent, StationsListNews> {
    override fun invoke(state: StationsListState, event: StationsListEvent): Akt<StationsListState, StationsListNews> {
        return when (event) {
            is StationsListEvent.Start -> reduceStart(state)
            is StationsListEvent.OnSearchQueryChanged -> reduceOnSearchQueryChanged(state, event)
        }
    }

    private fun reduceStart(state: StationsListState): Akt<StationsListState, StationsListNews> {
        return when (state) {
            is StationsListState.Loading,
            is StationsListState.Data -> Akt()
            is StationsListState.Empty -> Akt(
                state = StationsListState.Data(),
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
}
