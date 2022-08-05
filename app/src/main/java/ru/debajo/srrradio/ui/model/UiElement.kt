package ru.debajo.srrradio.ui.model

import androidx.compose.runtime.Stable

@Stable
interface UiElement {
    val id: String
    val contentType: String
}
