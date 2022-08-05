package ru.debajo.srrradio.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class UiStationElement(
    val station: UiStation,
    val playingState: UiStationPlayingState,
    val favorite: Boolean,
) : UiElement {
    override val id: String = station.id
    override val contentType: String = "UiStationElement"
}
