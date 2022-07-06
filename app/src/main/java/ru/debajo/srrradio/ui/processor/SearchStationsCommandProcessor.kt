package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.toUi

class SearchStationsCommandProcessor(
    private val searchStationsUseCase: SearchStationsUseCase,
) : CommandProcessor {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands
            .filterIsInstance<Action>()
            .distinctUntilChanged()
            .mapLatest { action ->
                when (action) {
                    is Action.Cancel -> CommandResult.EMPTY
                    is Action.Search -> searchSafe(action.query)
                }
            }
    }

    private suspend fun searchSafe(query: String): CommandResult {
        return runCatching {
            delay(500)
            searchStationsUseCase.search(query).convert().let { SearchResult(it) }
        }.getOrElse {
            CommandResult.EMPTY
        }
    }

    private fun List<Station>.convert(): List<UiStation> = map { it.toUi() }

    sealed interface Action : Command {
        data class Search(val query: String) : Action
        object Cancel : Action
    }

    data class SearchResult(val stations: List<UiStation>) : CommandResult
}
