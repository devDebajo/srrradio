package ru.debajo.srrradio.data.usecase

import ru.debajo.srrradio.data.db.dao.DbPlaylistDao
import ru.debajo.srrradio.data.db.dao.DbPlaylistMappingDao
import ru.debajo.srrradio.data.db.dao.DbStationDao
import ru.debajo.srrradio.data.model.DbPlaylistMapping
import ru.debajo.srrradio.data.model.toDb
import ru.debajo.srrradio.data.model.toDomain
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.model.Playlist

internal class LoadPlaylistUseCaseImpl(
    private val playlistDao: DbPlaylistDao,
    private val stationDao: DbStationDao,
    private val dbPlaylistMappingDao: DbPlaylistMappingDao,
) : LoadPlaylistUseCase {

    override suspend fun loadPlaylist(playlistId: String): Playlist? {
        val playlist = playlistDao.getPlaylist(playlistId) ?: return null
        val stations = stationDao.getStationsByPlaylist(playlistId)
        return Playlist(
            id = playlistId,
            name = playlist.name,
            stations = stations.map { it.toDomain() },
        )
    }

    override suspend fun createOrUpdate(playlist: Playlist) {
        playlistDao.insert(playlist.toDb())
        stationDao.insert(playlist.stations.map { it.toDb() })
        dbPlaylistMappingDao.insert(playlist.stations.map { DbPlaylistMapping(playlist.id, it.id) })
    }
}
