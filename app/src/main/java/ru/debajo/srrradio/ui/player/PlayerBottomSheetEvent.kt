package ru.debajo.srrradio.ui.player

sealed interface PlayerBottomSheetEvent {
    object Start : PlayerBottomSheetEvent

    object NextStation : PlayerBottomSheetEvent
    object PreviousStation : PlayerBottomSheetEvent

    object OnPlayPauseClick : PlayerBottomSheetEvent

    class OnSelectStation(val page: Int) : PlayerBottomSheetEvent
}
