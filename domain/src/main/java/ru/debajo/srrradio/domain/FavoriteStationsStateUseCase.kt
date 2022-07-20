package ru.debajo.srrradio.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import ru.debajo.srrradio.common.mutableLazySuspend
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository

interface FavoriteStationsStateUseCase {
    suspend fun isFavorite(stationId: String): Boolean

    fun observe(): Flow<List<Station>>
}

internal class FavoriteStationsStateUseCaseImpl(
    private val repository: FavoriteStationsRepository
) : FavoriteStationsStateUseCase {

    private val favoriteStationsIds = mutableLazySuspend {
        repository.getFavoriteStations().map { it.id }.toSet()
    }

    override suspend fun isFavorite(stationId: String): Boolean {
        return stationId in getFavoriteStationsIds()
    }

    private suspend fun getFavoriteStationsIds(): Set<String> = favoriteStationsIds.get()

    override fun observe(): Flow<List<Station>> {
        return repository.observeFavoriteStations().onEach { stations ->
            favoriteStationsIds.set(stations.map { it.id }.toSet())
        }
    }
}
