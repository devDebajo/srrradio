package ru.debajo.srrradio.ui.timer

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class SleepTimerViewModel(
    private val sleepTimer: SleepTimer,
) : ViewModel() {

    private val stateMutable: MutableStateFlow<SleepTimerState> = MutableStateFlow(SleepTimerState())
    val state: StateFlow<SleepTimerState> = stateMutable.asStateFlow()

    fun onButtonClick() {
        val state = stateMutable.value
        stateMutable.value = if (state.buttonIsSave) {
            sleepTimer.scheduleThrough(state.value)
            state.copy(buttonIsSave = false)
        } else {
            sleepTimer.cancel()
            state.copy(
                value = 0,
                buttonIsSave = true
            )
        }
    }

    fun onValueChange(value: Float) {
        stateMutable.value = stateMutable.value.copy(value = value.roundToInt())
    }

    fun onValueChangeFinished() {
        val state = stateMutable.value
        if (sleepTimer.anyScheduled) {
            sleepTimer.scheduleThrough(state.value)
        }
    }

    fun show() {
        if (!sleepTimer.anyScheduled) {
            stateMutable.value = SleepTimerState(
                displaying = true,
                value = 0,
                buttonIsSave = true,
            )
        } else {
            stateMutable.value = SleepTimerState(
                displaying = true,
                value = TimeUnit.MILLISECONDS.toMinutes(sleepTimer.millisLeft).toInt(),
                buttonIsSave = false,
            )
        }
    }

    fun onVisibleChanged(visible: Boolean) {
        stateMutable.value = stateMutable.value.copy(displaying = visible)
    }

    companion object {
        val Local: ProvidableCompositionLocal<SleepTimerViewModel> = staticCompositionLocalOf { TODO() }
    }
}
