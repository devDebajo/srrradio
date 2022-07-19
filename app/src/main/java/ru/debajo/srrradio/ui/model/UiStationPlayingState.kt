package ru.debajo.srrradio.ui.model

import ru.debajo.srrradio.media.RadioPlayer

enum class UiStationPlayingState {
    BUFFERING,
    PLAYING,
    NONE,
}

fun RadioPlayer.PlaybackState.toUi(): UiStationPlayingState {
    return when (this) {
        RadioPlayer.PlaybackState.PAUSED -> UiStationPlayingState.NONE
        RadioPlayer.PlaybackState.BUFFERING -> UiStationPlayingState.BUFFERING
        RadioPlayer.PlaybackState.PLAYING -> UiStationPlayingState.PLAYING
    }
}