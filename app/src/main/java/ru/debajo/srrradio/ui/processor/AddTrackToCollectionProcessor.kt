package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.domain.TracksCollectionUseCase
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.toDomain

class AddTrackToCollectionProcessor(
    private val tracksCollectionUseCase: TracksCollectionUseCase
) : CommandProcessor {

    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands.filterIsInstance<Save>()
            .map {
                tracksCollectionUseCase.save(it.title, it.station.toDomain())
                CommandResult.EMPTY
            }
    }

    class Save(val title: String, val station: UiStation) : Command
}