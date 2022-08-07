package ru.debajo.srrradio.domain

import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository

interface UpdateFavoriteStationStateUseCase {
    suspend fun update(station: Station, inFavorite: Boolean)
}

internal class UpdateFavoriteStationStateUseCaseImpl(
    private val repository: FavoriteStationsRepository,
) : UpdateFavoriteStationStateUseCase {

    override suspend fun update(station: Station, inFavorite: Boolean) {
        repository.updateFavoriteState(station, inFavorite)
    }
}
