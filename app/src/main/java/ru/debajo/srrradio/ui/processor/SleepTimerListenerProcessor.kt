package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.ui.host.main.timer.SleepTimer

class SleepTimerListenerProcessor(
    private val sleepTimer: SleepTimer,
) : CommandProcessor {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands
            .filterIsInstance<Start>()
            .flatMapLatest { sleepTimer.observeState() }
            .map { sleepTimerState ->
                if (!sleepTimerState.scheduled) {
                    SleepTimerStateChanged(false)
                } else {
                    SleepTimerStateChanged(
                        scheduled = true,
                        leftMinutes = sleepTimerState.leftMinutes,
                        leftSeconds = sleepTimerState.leftSeconds
                    )
                }
            }
    }

    object Start : Command

    class SleepTimerStateChanged(
        val scheduled: Boolean,
        val leftMinutes: Int = 0,
        val leftSeconds: Int = 0,
    ) : CommandResult
}