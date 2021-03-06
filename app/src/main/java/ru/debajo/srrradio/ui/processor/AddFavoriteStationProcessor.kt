package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCase

class AddFavoriteStationProcessor(
    private val useCase: UpdateFavoriteStationStateUseCase,
) : CommandProcessor {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands
            .filterIsInstance<Update>()
            .mapLatest {
                useCase.update(it.stationId, it.favorite)
                CommandResult.EMPTY
            }
    }

    class Update(val stationId: String, val favorite: Boolean) : Command
}
