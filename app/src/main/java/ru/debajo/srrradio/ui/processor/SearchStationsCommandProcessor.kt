package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.toUi

class SearchStationsCommandProcessor(
    private val searchStationsUseCase: SearchStationsUseCase,
) : CommandProcessor {

    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands
            .filterIsInstance<Action>()
            .distinctUntilChanged()
            .mapLatest { action ->
                when (action) {
                    is Action.Cancel -> CommandResult.EMPTY
                    is Action.Search -> searchSafe(action.query) { searchStationsUseCase.search(action.query) }
                    is Action.SearchByUrl -> searchSafe("") { searchStationsUseCase.searchByUrl(action.url) }
                }
            }
    }

    private suspend fun searchSafe(query: String, searchBlock: suspend () -> List<Station>): CommandResult {
        return runCatchingNonCancellation {
            delay(500)
            SearchResult(query, searchBlock().convert())
        }.getOrElse { CommandResult.EMPTY }
    }

    private fun List<Station>.convert(): List<UiStation> = map { it.toUi() }

    sealed interface Action : Command {
        data class Search(val query: String) : Action
        data class SearchByUrl(val url: String) : Action
        object Cancel : Action
    }

    data class SearchResult(
        val query: String,
        val stations: List<UiStation>
    ) : CommandResult
}
