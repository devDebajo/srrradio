package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCase
import ru.debajo.srrradio.domain.model.Station

class AddFavoriteStationProcessor(
    private val useCase: UpdateFavoriteStationStateUseCase,
) : CommandProcessor {

    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands
            .filterIsInstance<Update>()
            .mapLatest {
                useCase.update(it.station, it.favorite)
                CommandResult.EMPTY
            }
    }

    class Update(val station: Station, val favorite: Boolean) : Command
}
