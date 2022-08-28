package ru.debajo.srrradio.domain.di

import android.content.Context
import android.location.LocationManager
import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.ParseM3uUseCase
import ru.debajo.srrradio.domain.SyncUseCase
import ru.debajo.srrradio.domain.UserStationUseCase
import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository
import ru.debajo.srrradio.domain.repository.SearchStationsRepository
import ru.debajo.srrradio.domain.repository.TracksCollectionRepository

interface DomainDependencies {
    val context: Context
    val locationManager: LocationManager

    val searchStationsRepository: SearchStationsRepository
    val favoriteStationsRepository: FavoriteStationsRepository
    val lastStationUseCase: LastStationUseCase
    val loadPlaylistUseCase: LoadPlaylistUseCase
    val userStationUseCase: UserStationUseCase
    val tracksCollectionRepository: TracksCollectionRepository
    val parseM3uUseCase: ParseM3uUseCase
    val syncUseCase: SyncUseCase
}
