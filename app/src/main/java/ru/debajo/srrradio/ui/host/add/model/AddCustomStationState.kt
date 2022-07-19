package ru.debajo.srrradio.ui.host.add.model

import android.util.Patterns

data class AddCustomStationState(
    val stream: String = "",
    val searching: Boolean = false,
    val name: String = "",
) {
    val canEditName: Boolean = isStreamValid()

    val canSave: Boolean = canEditName && name.isNotEmpty()

    private fun isStreamValid(): Boolean {
        return Patterns.WEB_URL.matcher(stream).matches()
    }
}
