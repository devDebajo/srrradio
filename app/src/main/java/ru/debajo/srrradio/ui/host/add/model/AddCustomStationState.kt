package ru.debajo.srrradio.ui.host.add.model

import android.util.Patterns
import androidx.compose.ui.text.input.TextFieldValue
import ru.debajo.srrradio.ui.ext.Empty

data class AddCustomStationState(
    val stream: TextFieldValue = TextFieldValue.Empty,
    val searching: Boolean = false,
    val name: TextFieldValue = TextFieldValue.Empty,
) {
    val canEditName: Boolean = isStreamValid()

    val canSave: Boolean = canEditName && name.text.isNotEmpty()

    private fun isStreamValid(): Boolean {
        return Patterns.WEB_URL.matcher(stream.text).matches()
    }
}
