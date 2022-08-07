package ru.debajo.srrradio.ui.ext

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalView

private class FixedHapticFeedback(private val view: View) : HapticFeedback {
    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
        val feedbackConstant = when {
            hapticFeedbackType == HapticFeedbackType.TextHandleMove && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 ->
                HapticFeedbackConstants.TEXT_HANDLE_MOVE

            hapticFeedbackType == HapticFeedbackType.LongPress -> HapticFeedbackConstants.LONG_PRESS

            else -> HapticFeedbackConstants.CLOCK_TICK
        }

        view.performHapticFeedback(feedbackConstant, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
    }
}

@Composable
fun rememberFixedHapticFeedback(): HapticFeedback {
    val view = LocalView.current
    return remember(view) { FixedHapticFeedback(view) }
}

fun HapticFeedback.longPress() {
    performHapticFeedback(HapticFeedbackType.LongPress)
}


fun HapticFeedback.textHandleMove() {
    performHapticFeedback(HapticFeedbackType.TextHandleMove)
}
