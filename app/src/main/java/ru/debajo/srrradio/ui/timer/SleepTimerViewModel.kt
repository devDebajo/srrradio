package ru.debajo.srrradio.ui.timer

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

class SleepTimerViewModel(
    private val sleepTimer: SleepTimer,
) : ViewModel() {

    private val stateMutable: MutableStateFlow<SleepTimerState> = MutableStateFlow(SleepTimerState())
    val state: StateFlow<SleepTimerState> = stateMutable.asStateFlow()

    init {
        viewModelScope.launch {
            sleepTimer.observeState().collect { timerState ->
                stateMutable.value = stateMutable.value.copy(
                    seconds = timerState.leftSeconds,
                    buttonIsSave = !timerState.scheduled
                )
            }
        }
    }

    fun onButtonClick() {
        val state = stateMutable.value
        stateMutable.value = if (state.buttonIsSave) {
            sleepTimer.scheduleThrough(state.seconds)
            state.copy(buttonIsSave = false)
        } else {
            sleepTimer.cancel()
            state.copy(
                seconds = 0,
                buttonIsSave = true
            )
        }
    }

    fun onValueChange(value: Float) {
        val minutes = value.roundToLong()
        val seconds = TimeUnit.MINUTES.toSeconds(minutes)
        stateMutable.value = stateMutable.value.copy(seconds = seconds)
    }

    fun onValueChangeFinished() {
        val state = stateMutable.value
        if (sleepTimer.anyScheduled) {
            sleepTimer.scheduleThrough(state.seconds)
        }
    }

    fun show() {
        stateMutable.value = stateMutable.value.copy(displaying = true)
    }

    fun onVisibleChanged(visible: Boolean) {
        stateMutable.value = stateMutable.value.copy(displaying = visible)
    }

    companion object {
        val Local: ProvidableCompositionLocal<SleepTimerViewModel> = staticCompositionLocalOf { TODO() }
    }
}
