package ru.debajo.srrradio.ui.host.main.player.model

sealed interface PlayerBottomSheetEvent {
    data object Start : PlayerBottomSheetEvent
    data object NextStation : PlayerBottomSheetEvent
    data object PreviousStation : PlayerBottomSheetEvent
    data object OnPlayPauseClick : PlayerBottomSheetEvent
    class UpdateStationFavorite(val favorite: Boolean) : PlayerBottomSheetEvent
    class OnSelectStation(val page: Int) : PlayerBottomSheetEvent
    class AddTrackToCollection(val title: String) : PlayerBottomSheetEvent
}
