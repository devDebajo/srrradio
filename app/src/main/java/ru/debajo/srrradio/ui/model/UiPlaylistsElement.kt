package ru.debajo.srrradio.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class UiPlaylistsElement(
    val list: List<UiPlaylistIcon>,
) : UiElement {
    override val id: String = "UiPlaylistsElement"
    override val contentType: String = "UiPlaylistsElement"
}
