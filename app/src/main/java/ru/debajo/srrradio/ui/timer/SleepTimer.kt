package ru.debajo.srrradio.ui.timer

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

class SleepTimer {

    private val taskMutable: MutableStateFlow<Task> = MutableStateFlow(Task.None)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeState(): Flow<State> {
        return taskMutable.flatMapLatest { task ->
            when (task) {
                is Task.None -> flowOf(State(false, 0))
                is Task.Pause -> stateTicker(task.at)
            }
        }
    }

    private fun stateTicker(at: Long): Flow<State> {
        return flow {
            val deltaMs = at - System.currentTimeMillis()
            var secsLeft = TimeUnit.MILLISECONDS.toSeconds(deltaMs)
            do {
                emit(State(true, (secsLeft--).toInt()))
                delay(1000)
            } while (secsLeft >= 0)
        }
    }

    suspend fun awaitPause(onPause: () -> Unit) {
        taskMutable
            .filterIsInstance<Task.Pause>()
            .collect {
                runCatching {
                    delay(it.at - System.currentTimeMillis())
                    onPause()
                    taskMutable.value = Task.None
                }
            }
    }

    val anyScheduled: Boolean
        get() {
            val task = taskMutable.value as? Task.Pause ?: return false
            val now = System.currentTimeMillis()
            return task.at > now
        }

    fun scheduleThrough(seconds: Int) {
        cancel()

        val millis = TimeUnit.SECONDS.toMillis(seconds.toLong())
        val at = System.currentTimeMillis() + millis
        taskMutable.value = Task.Pause(at)
    }

    fun cancel() {
        taskMutable.value = Task.None
    }

    private sealed interface Task {
        object None : Task
        data class Pause(val at: Long) : Task
    }

    data class State(
        val scheduled: Boolean,
        val totalLeftSeconds: Int,
    ) {
        val leftMinutes: Int = TimeUnit.SECONDS.toMinutes(totalLeftSeconds.toLong()).toInt()
        val leftSeconds: Int = totalLeftSeconds - (leftMinutes * 60)
    }
}
