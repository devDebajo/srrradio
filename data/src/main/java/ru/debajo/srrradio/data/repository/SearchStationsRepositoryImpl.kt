package ru.debajo.srrradio.data.repository

import java.util.concurrent.TimeUnit
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.common.utils.toTimber
import ru.debajo.srrradio.data.db.dao.DbStationDao
import ru.debajo.srrradio.data.model.RemoteStation
import ru.debajo.srrradio.data.model.toDb
import ru.debajo.srrradio.data.model.toDomain
import ru.debajo.srrradio.data.service.ServiceHolder
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

internal class SearchStationsRepositoryImpl(
    private val serviceHolder: ServiceHolder,
    private val stationsForMapLastUpdatePreference: StationsForMapLastUpdatePreference,
    private val dbStationDao: DbStationDao,
) : SearchStationsRepository {

    override suspend fun search(query: String): List<Station> {
        return serviceHolder.createService()
            .search(query = query.trim(), hideBroken = true)
            .convertToDomain()
    }

    override suspend fun getAllStationsForMap(): List<Station> {
        val dbStationsWithLocation = dbStationDao.getAllWithLocation()
        if (dbStationsWithLocation.isNotEmpty()) {
            return dbStationsWithLocation.map { it.toDomain() }
        }
        return loadStationsWithGeoInfo()
    }

    override suspend fun searchByUrl(url: String): List<Station> {
        return serviceHolder.createService()
            .byUrl(url.trim())
            .convertToDomain()
    }

    override suspend fun searchNew(limit: Int): List<Station> {
        return serviceHolder.createService()
            .newStations(limit = limit, hideBroken = true)
            .convertToDomain()
    }

    override suspend fun searchPopular(limit: Int): List<Station> {
        return serviceHolder.createService()
            .popularStations(limit = limit, hideBroken = true)
            .convertToDomain()
    }

    override suspend fun warmUpStationsIfNeed() {
        val lastUpdate = stationsForMapLastUpdatePreference.get()
        if (System.currentTimeMillis() - lastUpdate < STATIONS_FOR_MAP_DURATION_MS) {
            return
        }

        runCatchingNonCancellation { loadStationsWithGeoInfo() }.toTimber()
    }

    private suspend fun loadStationsWithGeoInfo(): List<Station> {
        val toPersist = serviceHolder.createService()
            .search(hasGeoInfo = true, hideBroken = true)
            .asSequence()
            .filter { it.latitude != null && it.longitude != null }
            .map { it.toDb() }
            .toList()
        dbStationDao.insert(toPersist)
        stationsForMapLastUpdatePreference.set(System.currentTimeMillis())
        return toPersist.filter { it.alive }.map { it.toDomain() }
    }

    private fun List<RemoteStation>.convertToDomain(): List<Station> {
        return asSequence()
            .filterAlive()
            .convert()
            .toList()
    }

    private fun Sequence<RemoteStation>.filterAlive(): Sequence<RemoteStation> {
        return filter { it.health == 1 }
    }

    private fun Sequence<RemoteStation>.convert(): Sequence<Station> {
        return map { station -> station.toDomain() }
    }

    private companion object {
        val STATIONS_FOR_MAP_DURATION_MS: Long = TimeUnit.DAYS.toMillis(1)
    }
}
