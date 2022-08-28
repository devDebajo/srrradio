package ru.debajo.srrradio.domain

import kotlinx.coroutines.flow.Flow
import ru.debajo.srrradio.domain.model.CollectionItem
import ru.debajo.srrradio.domain.model.Station

interface TracksCollectionUseCase {
    suspend fun save(track: String, station: Station)

    suspend fun save(collection: List<CollectionItem>)

    suspend fun delete(track: String)

    fun observe(): Flow<List<CollectionItem>>

    suspend fun get(): List<CollectionItem>
}
