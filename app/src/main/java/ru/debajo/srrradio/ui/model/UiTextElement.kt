package ru.debajo.srrradio.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class UiTextElement(val text: String) : UiElement {
    override val id: String = "UiTextElement_$text"
    override val contentType: String = "UiTextElement"
}
