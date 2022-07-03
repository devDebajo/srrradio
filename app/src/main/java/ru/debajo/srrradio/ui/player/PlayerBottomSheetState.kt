package ru.debajo.srrradio.ui.player

import androidx.compose.runtime.Immutable
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.UiStationPlayingState

@Immutable
data class PlayerBottomSheetState(
    val showBottomSheet: Boolean = false,
    val currentStationNameOrEmpty: String = "",
    val playingState: UiStationPlayingState = UiStationPlayingState.NONE,
    val currentStationIndex: Int = -1,
    val stations: List<UiStation> = emptyList(),
    val playing: Boolean = false,
) {
    val hasPreviousStation: Boolean = stations.getOrNull(currentStationIndex - 1) != null
    val hasNextStation: Boolean = stations.getOrNull(currentStationIndex + 1) != null
}
