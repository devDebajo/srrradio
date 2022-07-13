package ru.debajo.srrradio.ui.timer

import java.util.concurrent.TimeUnit

data class SleepTimerState(
    val displaying: Boolean = false,
    val seconds: Long = 0L,
    val buttonIsSave: Boolean = true,
    val minValue: Float = 0f,
    val maxValue: Float = 90f,
    val steps: Int = 8,
) {
    val minutes: Float = TimeUnit.SECONDS.toMinutes(seconds).toFloat()
}
