package ru.debajo.srrradio.ui.model

import androidx.compose.runtime.Immutable
import ru.debajo.srrradio.domain.model.Playlist

@Immutable
data class UiPlaylist(
    val id: String,
    val name: String,
    val stations: List<UiStation>,
) {
    operator fun contains(station: UiStation): Boolean = stations.any { it.id == station.id }
}

fun Playlist.toUi(): UiPlaylist {
    return UiPlaylist(
        id = id,
        name = name,
        stations = stations.map { it.toUi() },
    )
}

fun UiPlaylist.toDomain(): Playlist {
    return Playlist(
        id = id,
        name = name,
        stations = stations.map { it.toDomain() },
    )
}
