package ru.debajo.srrradio.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import ru.debajo.srrradio.common.mutableLazySuspend
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.common.utils.toTimber
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository

interface FavoriteStationsStateUseCase {
    suspend fun isFavorite(stationId: String): Boolean

    suspend fun save(stations: List<Station>)

    fun observe(): Flow<List<Station>>

    suspend fun get(): List<Station>

    suspend fun updateStations(stationIds: List<String>)

    suspend fun reloadFromNetwork()
}

internal class FavoriteStationsStateUseCaseImpl(
    private val searchStationsUseCase: SearchStationsUseCase,
    private val repository: FavoriteStationsRepository
) : FavoriteStationsStateUseCase {

    private val favoriteStationsIds = mutableLazySuspend {
        repository.getFavoriteStations().map { it.id }.toSet()
    }

    override suspend fun isFavorite(stationId: String): Boolean {
        return stationId in getFavoriteStationsIds()
    }

    override suspend fun save(stations: List<Station>) {
        repository.save(stations)
    }

    private suspend fun getFavoriteStationsIds(): Set<String> = favoriteStationsIds.get()

    override fun observe(): Flow<List<Station>> {
        return repository.observeFavoriteStations().onEach { stations ->
            favoriteStationsIds.set(stations.map { it.id }.toSet())
        }
    }

    override suspend fun get(): List<Station> {
        return repository.getFavoriteStations()
    }

    override suspend fun updateStations(stationIds: List<String>) {
        repository.updateStations(stationIds)
    }

    override suspend fun reloadFromNetwork() {
        runCatchingNonCancellation {
            val ids = get().map { it.id }.distinct()
            if (ids.isNotEmpty()) {
                searchStationsUseCase.reloadToCache(ids)
            }
        }.toTimber()
    }
}
