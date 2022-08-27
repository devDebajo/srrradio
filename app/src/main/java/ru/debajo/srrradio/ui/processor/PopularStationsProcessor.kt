package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.toUi

class PopularStationsProcessor(
    private val searchStationsUseCase: SearchStationsUseCase,
) : CommandProcessor {

    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands.filterIsInstance<Load>()
            .mapLatest {
                runCatching {
                    searchStationsUseCase.searchPopular(20)
                        .map { it.toUi() }
                        .let(::Loaded)
                }
                    .getOrElse { CommandResult.EMPTY }
            }
    }

    object Load : Command

    class Loaded(val stations: List<UiStation>) : CommandResult
}