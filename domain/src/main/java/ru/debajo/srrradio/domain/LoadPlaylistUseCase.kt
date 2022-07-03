package ru.debajo.srrradio.domain

import ru.debajo.srrradio.domain.model.Playlist

interface LoadPlaylistUseCase {
    suspend fun loadPlaylist(playlistId: String): Playlist?

    suspend fun createOrUpdate(playlist: Playlist)
}
