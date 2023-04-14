package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
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

    @OptIn(FlowPreview::class)
    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands.filterIsInstance<Task>()
            .flatMapConcat { task -> handle(task) }
    }

    private suspend fun handle(task: Task): Flow<CommandResult> {
        return when (task) {
            is Task.CheckUpdate -> {
                if (checkAppUpdateUseCase.hasUpdate()) {
                    flowOf(Result.HasUpdate)
                } else {
                    flowOf(CommandResult.EMPTY)
                }
            }
            is Task.UpdateFlow -> {
                appUpdateFlowHelper.updateAppAsFlow().map { progress ->
                    when (progress) {
                        is AppUpdateFlowHelper.UpdateProgress.Failed -> Result.UpdateFailed
                        is AppUpdateFlowHelper.UpdateProgress.Loaded -> Result.UpdateSuccess
                        is AppUpdateFlowHelper.UpdateProgress.Loading -> Result.LoadingUpdate(progress.progress)
                    }
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

        class LoadingUpdate(val progress: Float) : Result

        object UpdateSuccess : Result

        object UpdateFailed : Result
    }
}
