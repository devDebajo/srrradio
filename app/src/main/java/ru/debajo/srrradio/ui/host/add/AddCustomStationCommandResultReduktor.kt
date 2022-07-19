package ru.debajo.srrradio.ui.host.add

import ru.debajo.reduktor.Akt
import ru.debajo.reduktor.CommandResult
import ru.debajo.reduktor.Reduktor
import ru.debajo.srrradio.ui.host.add.model.AddCustomStationNews
import ru.debajo.srrradio.ui.host.add.model.AddCustomStationState
import ru.debajo.srrradio.ui.processor.SaveCustomStationProcessor
import ru.debajo.srrradio.ui.processor.SearchStationsCommandProcessor

class AddCustomStationCommandResultReduktor : Reduktor<AddCustomStationState, CommandResult, AddCustomStationNews> {
    override fun invoke(state: AddCustomStationState, event: CommandResult): Akt<AddCustomStationState, AddCustomStationNews> {
        return when (event) {
            is SearchStationsCommandProcessor.SearchResult -> reduceSearchResult(state, event)
            is SaveCustomStationProcessor.Saved -> reduceCustomStationSaved()
            else -> Akt()
        }
    }

    private fun reduceSearchResult(
        state: AddCustomStationState,
        event: SearchStationsCommandProcessor.SearchResult
    ): Akt<AddCustomStationState, AddCustomStationNews> {
        return Akt(
            state = state.copy(
                searching = false,
                name = event.stations.firstOrNull()?.name.orEmpty()
            )
        )
    }

    private fun reduceCustomStationSaved(): Akt<AddCustomStationState, AddCustomStationNews> {
        return Akt(news = listOf(AddCustomStationNews.Close))
    }
}
