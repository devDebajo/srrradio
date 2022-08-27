package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.domain.FavoriteStationsStateUseCase
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.toUi

class ListenFavoriteStationsProcessor(
    private val useCase: FavoriteStationsStateUseCase,
) : CommandProcessor {

    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands
            .filterIsInstance<Listen>()
            .flatMapLatest { useCase.observe() }
            .map { stations -> Result(stations.map { it.toUi() }) }
    }

    object Listen : Command

    class Result(val stations: List<UiStation>) : CommandResult
}