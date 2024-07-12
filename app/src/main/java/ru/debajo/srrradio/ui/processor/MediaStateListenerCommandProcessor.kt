package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.media.model.MediaState

class MediaStateListenerCommandProcessor(
    private val mediaController: MediaController,
) : CommandProcessor {

    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands
            .filterIsInstance<ListenerCommand>()
            .distinctUntilChanged()
            .flatMapLatest { command ->
                when (command) {
                    ListenerCommand.Stop -> flowOf(CommandResult.EMPTY)
                    ListenerCommand.Start -> { mediaController.state.map { OnNewMediaState(it) } }
                }
            }
    }

    sealed interface ListenerCommand : Command {
        data object Start : ListenerCommand
        data object Stop : ListenerCommand
    }

    data class OnNewMediaState(val state: MediaState) : CommandResult
}
