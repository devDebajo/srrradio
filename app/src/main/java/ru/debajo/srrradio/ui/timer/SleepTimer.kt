package ru.debajo.srrradio.ui.timer

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import java.util.concurrent.TimeUnit

class SleepTimer {

    private val taskMutable: MutableStateFlow<Task> = MutableStateFlow(Task.None)

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

    val millisLeft: Long
        get() {
            return when (val task = taskMutable.value) {
                is Task.None -> 0
                is Task.Pause -> {
                    val leftMillis = task.at - System.currentTimeMillis()
                    leftMillis.coerceAtLeast(0)
                }
            }
        }

    val anyScheduled: Boolean
        get() {
            val task = taskMutable.value as? Task.Pause ?: return false
            val now = System.currentTimeMillis()
            return task.at > now
        }

    fun scheduleThrough(minutes: Int) {
        cancel()

        val millis = TimeUnit.MINUTES.toMillis(minutes.toLong())
        val at = System.currentTimeMillis() + millis
        taskMutable.value = Task.Pause(at)
    }

    fun cancel() {
        taskMutable.value = Task.None
    }

    sealed interface Task {
        object None : Task
        data class Pause(val at: Long) : Task
    }
}
