package ru.debajo.srrradio.domain.di

import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

interface DomainDependencies {
    val searchStationsRepository: SearchStationsRepository
    val lastStationUseCase: LastStationUseCase
    val loadPlaylistUseCase: LoadPlaylistUseCase
}
