package ru.debajo.srrradio.domain.repository

import ru.debajo.srrradio.domain.model.CollectionItem
import ru.debajo.srrradio.domain.model.Station

interface SyncRepository {
    suspend fun getFavoriteStations(userId: String): List<Station>

    suspend fun saveFavoriteStations(userId: String, stations: List<Station>)

    suspend fun getCollection(userId: String): List<CollectionItem>

    suspend fun saveCollection(userId: String, collection: List<CollectionItem>)
}
