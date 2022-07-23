package ru.debajo.srrradio.domain.di

import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.UserStationUseCase
import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository
import ru.debajo.srrradio.domain.repository.SearchStationsRepository
import ru.debajo.srrradio.domain.repository.TracksCollectionRepository

interface DomainDependencies {
    val searchStationsRepository: SearchStationsRepository
    val favoriteStationsRepository: FavoriteStationsRepository
    val lastStationUseCase: LastStationUseCase
    val loadPlaylistUseCase: LoadPlaylistUseCase
    val userStationUseCase: UserStationUseCase
    val tracksCollectionRepository: TracksCollectionRepository
}
