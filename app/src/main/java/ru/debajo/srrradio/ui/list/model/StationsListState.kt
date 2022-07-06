package ru.debajo.srrradio.ui.list.model

import androidx.compose.runtime.Immutable
import ru.debajo.srrradio.model.MediaState
import ru.debajo.srrradio.ui.model.UiElement
import ru.debajo.srrradio.ui.model.UiPlaylist
import ru.debajo.srrradio.ui.model.UiStation

@Immutable
data class StationsListState(
    val searchQuery: String = "",

    // local playlist, may be not in MediaState
    val playlist: UiPlaylist? = null,
    val mediaState: MediaState = MediaState.None,
    val uiElements: List<UiElement> = emptyList(),
    val favoriteStations: List<UiStation> = emptyList(),
)
