package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.ui.processor.interactor.UserStationsInteractor

class SaveCustomStationProcessor(
    private val userStationsInteractor: UserStationsInteractor,
) : CommandProcessor {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands
            .filterIsInstance<Save>()
            .mapLatest {
                runCatching {
                    userStationsInteractor.add(it.stream, it.name)
                    Saved
                }.getOrElse { CommandResult.EMPTY }
            }
    }

    class Save(val stream: String, val name: String) : Command

    object Saved : CommandResult
}
