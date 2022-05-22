package ru.debajo.srrradio.ui.list.reduktor.processor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import ru.debajo.srrradio.RadioPlayer
import ru.debajo.srrradio.common.presentation.Command
import ru.debajo.srrradio.common.presentation.CommandProcessor
import ru.debajo.srrradio.common.presentation.CommandResult

@OptIn(FlowPreview::class)
class PlayerStateListenerCommandProcessor(
    private val radioPlayer: RadioPlayer,
) : CommandProcessor {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands
            .filterIsInstance<ListenerCommand>()
            .distinctUntilChanged()
            .flatMapLatest { command ->
                when (command) {
                    ListenerCommand.Stop -> flowOf(CommandResult.EMPTY)
                    ListenerCommand.Start -> radioPlayer.states.map { OnNewPlayerState(it) }
                }
            }
    }

    sealed interface ListenerCommand : Command {
        object Start : ListenerCommand
        object Stop : ListenerCommand
    }

    data class OnNewPlayerState(val state: RadioPlayer.State) : CommandResult
}
