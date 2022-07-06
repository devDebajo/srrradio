package ru.debajo.srrradio.domain

import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository

interface UpdateFavoriteStationStateUseCase {
    suspend fun update(stationId: String, inFavorite: Boolean)
}

internal class UpdateFavoriteStationStateUseCaseImpl(
    private val repository: FavoriteStationsRepository,
) : UpdateFavoriteStationStateUseCase {

    override suspend fun update(stationId: String, inFavorite: Boolean) {
        repository.updateFavoriteState(stationId, inFavorite)
    }
}
