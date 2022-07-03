package ru.debajo.srrradio.data.usecase

import ru.debajo.srrradio.data.db.dao.DbPlaylistDao
import ru.debajo.srrradio.data.db.dao.DbStationDao
import ru.debajo.srrradio.data.model.DbStation
import ru.debajo.srrradio.data.model.toDb
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.model.Playlist
import ru.debajo.srrradio.domain.model.Station

internal class LoadPlaylistUseCaseImpl(
    private val playlistDao: DbPlaylistDao,
    private val stationDao: DbStationDao,
) : LoadPlaylistUseCase {

    override suspend fun loadPlaylist(playlistId: String): Playlist? {
        val playlist = playlistDao.getPlaylist(playlistId) ?: return null
        val stations = stationDao.getStationsByPlaylist(playlistId)
        return Playlist(
            id = playlistId,
            name = playlist.name,
            stations = stations.map { it.convert() },
        )
    }

    override suspend fun createOrUpdate(playlist: Playlist) {
        playlistDao.insert(playlist.toDb())
        stationDao.insert(playlist.stations.map { it.toDb() })
    }

    private fun DbStation.convert(): Station {
        return Station(
            id = id,
            name = name,
            stream = stream,
            image = image,
        )
    }
}
