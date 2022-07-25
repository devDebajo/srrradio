package ru.debajo.srrradio.ui.processor.interactor

import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCase
import ru.debajo.srrradio.domain.UserStationUseCase
import ru.debajo.srrradio.domain.model.Station

class UserStationsInteractor(
    private val userStationUseCase: UserStationUseCase,
    private val updateFavoriteStationStateUseCase: UpdateFavoriteStationStateUseCase
) {
    suspend fun add(stream: String, name: String): Station {
        val station = userStationUseCase.create(stream, name)
        updateFavoriteStationStateUseCase.update(station.id, inFavorite = true)
        return station
    }
}
