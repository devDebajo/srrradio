package ru.debajo.srrradio.ui.host.add.model

import androidx.compose.ui.text.input.TextFieldValue

sealed interface AddCustomStationEvent {
    class OnStreamChanged(val stream: TextFieldValue) : AddCustomStationEvent
    class OnNameChanged(val name: TextFieldValue) : AddCustomStationEvent
    object Save : AddCustomStationEvent
}