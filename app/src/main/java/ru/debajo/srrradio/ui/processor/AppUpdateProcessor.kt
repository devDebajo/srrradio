package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.data.usecase.CheckAppUpdateUseCase
import ru.debajo.srrradio.update.AppUpdateFlowHelper

class AppUpdateProcessor(
    private val checkAppUpdateUseCase: CheckAppUpdateUseCase,
    private val appUpdateFlowHelper: AppUpdateFlowHelper,
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
            is Task.UpdateFlow -> {
                val success = appUpdateFlowHelper.updateApp()
                if (success) {
                    Result.UpdateSuccess
                } else {
                    Result.UpdateFailed
                }
            }
        }
    }

    sealed interface Task : Command {
        object CheckUpdate : Task
        object UpdateFlow : Task
    }

    sealed interface Result : CommandResult {
        object HasUpdate : Result
        object UpdateSuccess : Result
        object UpdateFailed : Result
    }
}
