package ru.debajo.srrradio.model

import ru.debajo.srrradio.ui.model.UiStationPlayingState

data class MediaStationInfo(
    val currentStationId: String,
    val playingState: UiStationPlayingState,
)
