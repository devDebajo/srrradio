package ru.debajo.srrradio.ui.common.alert

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

@Stable
class AlertDialogState(private val context: Context) {

    var content: Content? by mutableStateOf(null)
        private set

    fun alert(
        title: Int,
        content: Int,
        confirm: Int,
        dismiss: Int,
        onDismiss: () -> Unit = {},
        onConfirm: () -> Unit,
    ) {
        this.content = Content(
            title = context.getString(title),
            content = context.getString(content),
            confirmButton = context.getString(confirm),
            dismissButton = context.getString(dismiss),
            onDismiss = onDismiss,
            onConfirm = onConfirm,
        )
    }

    fun onDismiss() {
        val onDismiss = content?.onDismiss ?: return
        content = null
        onDismiss()
    }

    fun onConfirm() {
        val onConfirm = content?.onConfirm ?: return
        content = null
        onConfirm()
    }

    class Content(
        val title: String,
        val content: String,
        val confirmButton: String,
        val dismissButton: String,
        val onDismiss: () -> Unit,
        val onConfirm: () -> Unit,
    )
}

val LocalAlertDialogState = staticCompositionLocalOf<AlertDialogState> { TODO() }

@Composable
fun rememberAlertDialogState(): AlertDialogState {
    val context = LocalContext.current
    return remember(context) { AlertDialogState(context) }
}
