package ru.debajo.srrradio.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class UiStation(
    val id: String,
    val name: String,
    val stream: String,
    val image: String?,
)
