package ru.debajo.srrradio.ui.common.alert

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BoxScope.AlertDialogHost(
    modifier: Modifier = Modifier,
    state: AlertDialogState,
) {
    val content = state.content
    if (content != null) {
        AlertDialog(
            modifier = modifier.align(Alignment.Center),
            onDismissRequest = { state.onDismiss() },
            title = { Text(content.title) },
            text = { Text(content.content) },
            confirmButton = {
                TextButton(onClick = { state.onConfirm() }) {
                    Text(content.confirmButton)
                }
            },
            dismissButton = {
                TextButton(onClick = { state.onDismiss() }) {
                    Text(content.dismissButton)
                }
            }
        )
    }
}
