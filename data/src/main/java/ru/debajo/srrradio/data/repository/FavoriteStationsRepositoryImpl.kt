package ru.debajo.srrradio.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.debajo.srrradio.data.db.dao.DbFavoriteStationDao
import ru.debajo.srrradio.data.model.DbFavoriteStation
import ru.debajo.srrradio.data.model.toDomain
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository

internal class FavoriteStationsRepositoryImpl(
    private val dbFavoriteStationDao: DbFavoriteStationDao,
) : FavoriteStationsRepository {

    override suspend fun updateFavoriteState(stationId: String, inFavorite: Boolean) {
        if (inFavorite) {
            dbFavoriteStationDao.insert(DbFavoriteStation(stationId))
        } else {
            dbFavoriteStationDao.delete(stationId)
        }
    }

    override fun observeFavoriteStations(): Flow<List<Station>> {
        return dbFavoriteStationDao.observeStations().map { stations ->
            stations.map { station -> station.toDomain() }
        }
    }

    override suspend fun getFavoriteStations(): List<Station> {
        return dbFavoriteStationDao.getStations().map { station -> station.toDomain() }
    }
}