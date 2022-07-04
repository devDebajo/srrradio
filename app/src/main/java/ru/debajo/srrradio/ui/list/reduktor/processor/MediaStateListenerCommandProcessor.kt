package ru.debajo.srrradio.ui.list.reduktor.processor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.MediaController
import ru.debajo.srrradio.model.MediaState

class MediaStateListenerCommandProcessor(
    private val mediaController: MediaController,
) : CommandProcessor {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands
            .filterIsInstance<ListenerCommand>()
            .distinctUntilChanged()
            .flatMapLatest { command ->
                when (command) {
                    ListenerCommand.Stop -> flowOf(CommandResult.EMPTY)
                    ListenerCommand.Start -> {
                        mediaController.state.map { OnNewMediaState(it) }
                    }
                }
            }
    }

    sealed interface ListenerCommand : Command {
        object Start : ListenerCommand
        object Stop : ListenerCommand
    }

    data class OnNewMediaState(val state: MediaState) : CommandResult
}
