package ru.debajo.srrradio.domain.di

import ru.debajo.srrradio.domain.FavoriteStationsStateUseCase
import ru.debajo.srrradio.domain.FavoriteStationsStateUseCaseImpl
import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCase
import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCaseImpl
import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

internal interface DomainModule : DomainApi {

    fun provideSearchStationsUseCase(searchStationsRepository: SearchStationsRepository): SearchStationsUseCase {
        return searchStationsRepository
    }

    fun provideUpdateFavoriteStationStateUseCase(repository: FavoriteStationsRepository): UpdateFavoriteStationStateUseCase {
        return UpdateFavoriteStationStateUseCaseImpl(repository)
    }

    fun provideFavoriteStationsStateUseCase(repository: FavoriteStationsRepository): FavoriteStationsStateUseCase {
        return FavoriteStationsStateUseCaseImpl(repository)
    }

    class Impl(private val dependencies: DomainDependencies) : DomainModule {
        override val searchStationsUseCase: SearchStationsUseCase
            get() = provideSearchStationsUseCase(dependencies.searchStationsRepository)

        override val lastStationUseCase: LastStationUseCase
            get() = dependencies.lastStationUseCase

        override val loadPlaylistUseCase: LoadPlaylistUseCase
            get() = dependencies.loadPlaylistUseCase

        override val updateFavoriteStationStateUseCase: UpdateFavoriteStationStateUseCase
            get() = provideUpdateFavoriteStationStateUseCase(dependencies.favoriteStationsRepository)

        override val favoriteStationsStateUseCase: FavoriteStationsStateUseCase by lazy {
            provideFavoriteStationsStateUseCase(dependencies.favoriteStationsRepository)
        }
    }
}
