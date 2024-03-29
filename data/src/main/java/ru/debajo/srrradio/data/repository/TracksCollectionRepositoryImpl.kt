package ru.debajo.srrradio.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.debajo.srrradio.data.db.dao.DbTrackCollectionItemDao
import ru.debajo.srrradio.data.model.DbTrackCollectionItem
import ru.debajo.srrradio.domain.model.CollectionItem
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.domain.repository.TracksCollectionRepository

internal class TracksCollectionRepositoryImpl(
    private val dao: DbTrackCollectionItemDao
) : TracksCollectionRepository {

    override suspend fun save(track: String, station: Station) {
        dao.insert(
            DbTrackCollectionItem(
                name = track,
                stationId = station.id,
                stationName = station.name
            )
        )
    }

    override suspend fun save(collection: List<CollectionItem>) {
        dao.insert(collection.map {
            DbTrackCollectionItem(
                name = it.track,
                stationId = it.stationId,
                stationName = it.stationName
            )
        })
    }

    override suspend fun delete(track: String) {
        dao.delete(track)
    }

    override fun observe(): Flow<List<CollectionItem>> {
        return dao.observe().map { it.convert() }
    }

    override suspend fun get(): List<CollectionItem> {
        return dao.getAll().convert()
    }

    private fun List<DbTrackCollectionItem>.convert(): List<CollectionItem> {
        return map {
            CollectionItem(
                track = it.name,
                stationId = it.stationId,
                stationName = it.stationName
            )
        }
    }
}
