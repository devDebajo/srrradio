package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.data.usecase.CheckAppUpdateUseCase

class AppUpdateProcessor(
    private val checkAppUpdateUseCase: CheckAppUpdateUseCase,
) : CommandProcessor {

    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands.filterIsInstance<Task>()
            .map { task -> handle(task) }
    }

    private suspend fun handle(task: Task): CommandResult {
        return when (task) {
            is Task.CheckUpdate -> {
                if (checkAppUpdateUseCase.hasUpdate()) {
                    Result.HasUpdate
                } else {
                    CommandResult.EMPTY
                }
            }
        }
    }

    sealed interface Task : Command {
        object CheckUpdate : Task
    }

    sealed interface Result : CommandResult {
        object HasUpdate : Result
    }
}
