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
        when {
            hapticFeedbackType == HapticFeedbackType.LongPress || Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1 -> view.performHapticFeedback(
                HapticFeedbackConstants.LONG_PRESS,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )

            hapticFeedbackType == HapticFeedbackType.TextHandleMove -> {
                view.performHapticFeedback(
                    HapticFeedbackConstants.TEXT_HANDLE_MOVE,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                )
            }
        }
    }
}

@Composable
fun rememberFixedHapticFeedback(): HapticFeedback {
    val view = LocalView.current
    return remember(view) { FixedHapticFeedback(view) }
}
