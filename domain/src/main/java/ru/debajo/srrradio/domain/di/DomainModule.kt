package ru.debajo.srrradio.domain.di

import android.content.Context
import android.location.LocationManager
import ru.debajo.srrradio.domain.FavoriteStationsStateUseCase
import ru.debajo.srrradio.domain.FavoriteStationsStateUseCaseImpl
import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.ParseM3uUseCase
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.TracksCollectionUseCase
import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCase
import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCaseImpl
import ru.debajo.srrradio.domain.UserLocationUseCase
import ru.debajo.srrradio.domain.UserLocationUseCaseImpl
import ru.debajo.srrradio.domain.UserStationUseCase
import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository
import ru.debajo.srrradio.domain.repository.SearchStationsRepository
import ru.debajo.srrradio.domain.repository.TracksCollectionRepository

internal interface DomainModule : DomainApi {

    fun provideSearchStationsUseCase(searchStationsRepository: SearchStationsRepository): SearchStationsUseCase {
        return searchStationsRepository
    }

    fun provideTracksCollectionUseCase(repository: TracksCollectionRepository): TracksCollectionUseCase = repository

    fun provideUpdateFavoriteStationStateUseCase(repository: FavoriteStationsRepository): UpdateFavoriteStationStateUseCase {
        return UpdateFavoriteStationStateUseCaseImpl(repository)
    }

    fun provideFavoriteStationsStateUseCase(repository: FavoriteStationsRepository): FavoriteStationsStateUseCase {
        return FavoriteStationsStateUseCaseImpl(repository)
    }

    fun provideUserLocationUseCase(
        context: Context,
        locationManager: LocationManager,
    ): UserLocationUseCase = UserLocationUseCaseImpl(context, locationManager)

    class Impl(private val dependencies: DomainDependencies) : DomainModule {
        override val searchStationsUseCase: SearchStationsUseCase
            get() = provideSearchStationsUseCase(dependencies.searchStationsRepository)

        override val lastStationUseCase: LastStationUseCase
            get() = dependencies.lastStationUseCase

        override val loadPlaylistUseCase: LoadPlaylistUseCase
            get() = dependencies.loadPlaylistUseCase

        override val userStationUseCase: UserStationUseCase
            get() = dependencies.userStationUseCase

        override val updateFavoriteStationStateUseCase: UpdateFavoriteStationStateUseCase
            get() = provideUpdateFavoriteStationStateUseCase(dependencies.favoriteStationsRepository)

        override val favoriteStationsStateUseCase: FavoriteStationsStateUseCase by lazy {
            provideFavoriteStationsStateUseCase(dependencies.favoriteStationsRepository)
        }

        override val tracksCollectionUseCase: TracksCollectionUseCase
            get() = provideTracksCollectionUseCase(dependencies.tracksCollectionRepository)

        override val parseM3uUseCase: ParseM3uUseCase
            get() = dependencies.parseM3uUseCase

        override val userLocationUseCase: UserLocationUseCase
            get() = provideUserLocationUseCase(dependencies.context, dependencies.locationManager)
    }
}
