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

    override suspend fun delete(track: String) {
        dao.delete(track)
    }

    override suspend fun observe(): Flow<List<CollectionItem>> {
        return dao.observe().map { it.convert() }
    }

    private fun List<DbTrackCollectionItem>.convert(): List<CollectionItem> {
        return map {
            CollectionItem(
                track = it.name,
                stationName = it.stationName
            )
        }
    }
}
