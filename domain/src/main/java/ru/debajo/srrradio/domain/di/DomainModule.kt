package ru.debajo.srrradio.domain.di

import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

internal interface DomainModule : DomainApi {
    fun provideSearchStationsUseCase(searchStationsRepository: SearchStationsRepository): SearchStationsUseCase {
        return searchStationsRepository
    }

    class Impl(private val dependencies: DomainDependencies) : DomainModule {
        override val searchStationsUseCase: SearchStationsUseCase
            get() = provideSearchStationsUseCase(dependencies.searchStationsRepository)

        override val lastStationUseCase: LastStationUseCase
            get() = dependencies.lastStationUseCase

        override val loadPlaylistUseCase: LoadPlaylistUseCase
            get() = dependencies.loadPlaylistUseCase
    }
}
