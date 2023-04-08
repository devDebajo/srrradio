package ru.debajo.srrradio.ui.host.main.list.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import ru.debajo.srrradio.media.model.MediaState
import ru.debajo.srrradio.media.model.asLoaded
import ru.debajo.srrradio.ui.ext.Empty
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
        val collectionEmpty: Boolean = true,
        val fallBackPlaylist: UiPlaylist? = null,
        val hasAppUpdate: Boolean = false,
        val autoPlayed: Boolean = false,
    ) : StationsListState {
        override val uiElements: List<UiElement> = buildList {
            add(UiPlaylistsElement(DefaultMainTiles.getTiles(hasAppUpdate)))

            val playlist = (mediaState as? MediaState.Loaded)?.playlist ?: fallBackPlaylist
            if (playlist != null) {
                add(UiTextElement(playlist.name))
                addAll(
                    playlist.buildUiElements(
                        favoriteStationsIds = favoriteStationsIds,
                        mediaState = mediaState
                    )
                )
            }
        }
    }

    @Immutable
    data class InSearchMode(
        val searchQuery: TextFieldValue = TextFieldValue.Empty,
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

val StationsListState.idle: StationsListState.Idle
    get() = when (this) {
        is StationsListState.Idle -> this
        is StationsListState.InSearchMode -> idleState
    }

val StationsListState.mediaState: MediaState
    get() = idle.mediaState

val StationsListState.playlist: UiPlaylist?
    get() = when (this) {
        is StationsListState.Idle -> mediaState.asLoaded?.playlist ?: fallBackPlaylist
        is StationsListState.InSearchMode -> searchPlaylist
    }

val StationsListState.favoriteStations: List<UiStation>
    get() = idle.favoriteStations

val StationsListState.favoriteStationsIds: Set<String>
    get() = favoriteStations.map { it.id }.toSet()

val StationsListState.collectionEmpty: Boolean
    get() = idle.collectionEmpty

val StationsListState.searchQuery: TextFieldValue
    get() = when (this) {
        is StationsListState.Idle -> TextFieldValue.Empty
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
