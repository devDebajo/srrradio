package ru.debajo.srrradio.domain

import ru.debajo.srrradio.domain.model.Station

interface UserStationUseCase {
    suspend fun create(stream: String, name: String): Station
}