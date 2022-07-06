package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.toUi

@OptIn(FlowPreview::class)
class SearchStationsCommandProcessor(
    private val searchStationsUseCase: SearchStationsUseCase,
) : CommandProcessor {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands
            .filterIsInstance<SearchCommand>()
            .debounce(500)
            .distinctUntilChanged()
            .map { it.query }
            .mapLatest { query -> searchStationsUseCase.search(query).convert() }
            .map { SearchResult(it) }
    }

    private fun List<Station>.convert(): List<UiStation> = map { it.toUi() }

    data class SearchCommand(val query: String) : Command

    data class SearchResult(val stations: List<UiStation>) : CommandResult
}
