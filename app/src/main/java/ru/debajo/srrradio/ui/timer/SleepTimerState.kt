package ru.debajo.srrradio.ui.timer

data class SleepTimerState(
    val displaying: Boolean = false,
    val value: Int = 0,
    val buttonIsSave: Boolean = true,
)
