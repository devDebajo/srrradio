package ru.debajo.srrradio.ui.host.add.model

sealed interface AddCustomStationEvent {
    class OnStreamChanged(val stream: String) : AddCustomStationEvent
    class OnNameChanged(val name: String) : AddCustomStationEvent
    object Save : AddCustomStationEvent
}