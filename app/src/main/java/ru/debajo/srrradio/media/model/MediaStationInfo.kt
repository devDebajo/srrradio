package ru.debajo.srrradio.media.model

import ru.debajo.srrradio.ui.model.UiStationPlayingState

data class MediaStationInfo(
    val currentStationId: String,
    val playingState: UiStationPlayingState,
    val title: String?,
    val playerInitialized: Boolean,
)
