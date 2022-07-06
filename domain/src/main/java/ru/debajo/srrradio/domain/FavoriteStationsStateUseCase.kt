package ru.debajo.srrradio.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository

interface FavoriteStationsStateUseCase {
    suspend fun isFavorite(stationId: String): Boolean

    fun observe(): Flow<List<Station>>
}

internal class FavoriteStationsStateUseCaseImpl(
    private val repository: FavoriteStationsRepository
) : FavoriteStationsStateUseCase {

    private val mutex = Mutex()
    private var favoriteStationsIds: Set<String>? = null

    override suspend fun isFavorite(stationId: String): Boolean {
        return stationId in getFavoriteStationsIds()
    }

    private suspend fun getFavoriteStationsIds(): Set<String> {
        if (favoriteStationsIds == null) {
            mutex.withLock {
                if (favoriteStationsIds == null) {
                    favoriteStationsIds = repository.getFavoriteStations().map { it.id }.toSet()
                }
            }
        }
        return favoriteStationsIds.orEmpty()
    }

    override fun observe(): Flow<List<Station>> {
        return repository.observeFavoriteStations().onEach {
            mutex.withLock {
                favoriteStationsIds = it.map { it.id }.toSet()
            }
        }
    }
}
