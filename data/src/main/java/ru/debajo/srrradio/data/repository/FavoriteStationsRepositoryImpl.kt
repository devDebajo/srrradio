package ru.debajo.srrradio.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.debajo.srrradio.data.db.dao.DbFavoriteStationDao
import ru.debajo.srrradio.data.db.dao.DbStationDao
import ru.debajo.srrradio.data.model.DbFavoriteStation
import ru.debajo.srrradio.data.model.toDb
import ru.debajo.srrradio.data.model.toDomain
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository

internal class FavoriteStationsRepositoryImpl(
    private val dbStationDao: DbStationDao,
    private val dbFavoriteStationDao: DbFavoriteStationDao,
) : FavoriteStationsRepository {

    override suspend fun updateFavoriteState(station: Station, inFavorite: Boolean) {
        if (inFavorite) {
            dbStationDao.insert(station.toDb())
            dbFavoriteStationDao.insertWithOrder(station.id)
        } else {
            dbFavoriteStationDao.delete(station.id)
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

    override suspend fun save(stations: List<Station>) {
        dbStationDao.insert(stations.map { it.toDb() })
        val maxOrder = (dbFavoriteStationDao.getMaxOrder() ?: -1) + 1
        dbFavoriteStationDao.insert(stations.mapIndexed { index, station ->
            DbFavoriteStation(station.id, maxOrder + index)
        })
    }

    override suspend fun updateStations(stationIds: List<String>) {
        val toPersist = stationIds.mapIndexed { index, stationId -> DbFavoriteStation(stationId, index) }
        dbFavoriteStationDao.replace(toPersist)
    }
}
