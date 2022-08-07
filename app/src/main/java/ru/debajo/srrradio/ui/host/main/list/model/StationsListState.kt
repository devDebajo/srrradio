package ru.debajo.srrradio.ui.host.main.list.model

import androidx.compose.runtime.Immutable
import ru.debajo.srrradio.media.model.MediaState
import ru.debajo.srrradio.media.model.asLoaded
import ru.debajo.srrradio.ui.host.main.list.reduktor.buildUiElements
import ru.debajo.srrradio.ui.model.UiElement
import ru.debajo.srrradio.ui.model.UiPlaylist
import ru.debajo.srrradio.ui.model.UiPlaylistsElement
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.UiTextElement

@Immutable
sealed interface StationsListState {

    val uiElements: List<UiElement>

    @Immutable
    data class Idle(
        val mediaState: MediaState = MediaState.None,
        val favoriteStations: List<UiStation> = emptyList(),
        val collectionEmpty: Boolean = true
    ) : StationsListState {
        override val uiElements: List<UiElement> = buildList {
            add(UiPlaylistsElement(DefaultPlaylists.all))
            if (mediaState is MediaState.Loaded) {
                val playlist = mediaState.playlist
                add(UiTextElement(playlist.name))
                addAll(
                    mediaState.asLoaded?.playlist.buildUiElements(
                        favoriteStationsIds = favoriteStationsIds,
                        mediaState = mediaState
                    )
                )
            }
        }
    }

    @Immutable
    data class InSearchMode(
        val searchQuery: String = "",
        val stations: List<UiStation> = emptyList(),
        val idleState: Idle = Idle(),
        val searchPlaylist: UiPlaylist? = null
    ) : StationsListState {
        override val uiElements: List<UiElement> = searchPlaylist.buildUiElements(
            favoriteStationsIds = favoriteStationsIds,
            mediaState = mediaState
        )
    }
}

val StationsListState.mediaState: MediaState
    get() = when (this) {
        is StationsListState.Idle -> mediaState
        is StationsListState.InSearchMode -> idleState.mediaState
    }

val StationsListState.playlist: UiPlaylist?
    get() = when (this) {
        is StationsListState.Idle -> mediaState.asLoaded?.playlist
        is StationsListState.InSearchMode -> searchPlaylist
    }

val StationsListState.favoriteStations: List<UiStation>
    get() = when (this) {
        is StationsListState.Idle -> favoriteStations
        is StationsListState.InSearchMode -> idleState.favoriteStations
    }

val StationsListState.favoriteStationsIds: Set<String>
    get() = favoriteStations.map { it.id }.toSet()

val StationsListState.collectionEmpty: Boolean
    get() = when (this) {
        is StationsListState.Idle -> collectionEmpty
        is StationsListState.InSearchMode -> idleState.collectionEmpty
    }

val StationsListState.searchQuery: String
    get() = when (this) {
        is StationsListState.Idle -> ""
        is StationsListState.InSearchMode -> searchQuery
    }

fun StationsListState.toSearch(
    update: StationsListState.InSearchMode.() -> StationsListState.InSearchMode = { this }
): StationsListState.InSearchMode {
    return when (this) {
        is StationsListState.Idle -> {
            StationsListState.InSearchMode(idleState = this).update()
        }
        is StationsListState.InSearchMode -> update()
    }
}

fun StationsListState.toIdle(
    update: StationsListState.Idle.() -> StationsListState.Idle = { this }
): StationsListState.Idle {
    return when (this) {
        is StationsListState.Idle -> update()
        is StationsListState.InSearchMode -> idleState.update()
    }
}

fun StationsListState.updateIdle(
    update: StationsListState.Idle.() -> StationsListState.Idle = { this }
): StationsListState {
    return when (this) {
        is StationsListState.Idle -> update()
        is StationsListState.InSearchMode -> copy(idleState = idleState.update())
    }
}
