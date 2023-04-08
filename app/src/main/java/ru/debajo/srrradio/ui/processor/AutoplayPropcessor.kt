package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.rate.InitialAutoplayPreference

class AutoplayProcessor(
    private val mediaController: MediaController,
    private val initialAutoplayPreference: InitialAutoplayPreference
) : CommandProcessor {
    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands.filterIsInstance<Autoplay>()
            .map {
                if (initialAutoplayPreference.get()) {
                    mediaController.play()
                }
                CommandResult.EMPTY
            }
    }

    object Autoplay : Command
}