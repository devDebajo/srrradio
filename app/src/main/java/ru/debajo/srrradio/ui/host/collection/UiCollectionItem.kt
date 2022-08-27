package ru.debajo.srrradio.ui.host.collection

import androidx.compose.runtime.Immutable

@Immutable
data class UiCollectionItem(
    val track: String,
    val stationName: String,
)
