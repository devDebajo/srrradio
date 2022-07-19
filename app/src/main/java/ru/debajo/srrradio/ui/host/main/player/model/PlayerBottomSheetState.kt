package ru.debajo.srrradio.ui.host.main.player.model

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
    val favoriteStationsIds: Set<String> = emptySet(),
    val sleepTimerScheduled: Boolean = false,
    val sleepTimerLeftTimeFormatted: String? = null,
) {
    val playing: Boolean = playingState == UiStationPlayingState.PLAYING
    val hasPreviousStation: Boolean = stations.getOrNull(currentStationIndex - 1) != null
    val hasNextStation: Boolean = stations.getOrNull(currentStationIndex + 1) != null
    val currentStationInFavorite: Boolean = stations.getOrNull(currentStationIndex)?.let { it.id in favoriteStationsIds } == true
}
