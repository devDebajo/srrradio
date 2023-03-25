package ru.debajo.srrradio.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.debajo.srrradio.domain.model.Station

interface FavoriteStationsRepository {
    suspend fun updateFavoriteState(station: Station, inFavorite: Boolean)

    fun observeFavoriteStations(): Flow<List<Station>>

    suspend fun getFavoriteStations(): List<Station>

    suspend fun save(stations: List<Station>)

    suspend fun reorder(from: Int, to: Int)
}
