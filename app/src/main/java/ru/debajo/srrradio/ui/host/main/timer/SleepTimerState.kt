package ru.debajo.srrradio.ui.host.main.timer

data class SleepTimerState(
    val displaying: Boolean = false,
    val leftMinutes: Int = 0,
    val leftSeconds: Int = 0,
    val buttonIsSave: Boolean = true,
    val minValue: Float = 0f,
    val maxValue: Float = 90f,
    val steps: Int = 17,
) {
    val totalLeftSeconds: Int = leftMinutes * 60 + leftSeconds
}
