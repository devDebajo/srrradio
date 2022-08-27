package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.ui.model.UiPlaylist

class NewPlayCommandProcessor(
    private val mediaController: MediaController,
) : CommandProcessor {

    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands
            .filterIsInstance<NewPlay>()
            .mapLatest { command ->
                mediaController.newPlay(command.playlist, command.stationId, command.play)
                CommandResult.EMPTY
            }
    }

    class NewPlay(
        val playlist: UiPlaylist,
        val stationId: String,
        val play: Boolean
    ) : Command
}
