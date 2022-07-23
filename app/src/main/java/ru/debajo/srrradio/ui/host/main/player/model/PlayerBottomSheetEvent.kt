package ru.debajo.srrradio.ui.host.main.player.model

sealed interface PlayerBottomSheetEvent {
    object Start : PlayerBottomSheetEvent
    object NextStation : PlayerBottomSheetEvent
    object PreviousStation : PlayerBottomSheetEvent
    object OnPlayPauseClick : PlayerBottomSheetEvent
    class UpdateStationFavorite(val favorite: Boolean) : PlayerBottomSheetEvent
    class OnSelectStation(val page: Int) : PlayerBottomSheetEvent
    class AddTrackToCollection(val title: String) : PlayerBottomSheetEvent
}
