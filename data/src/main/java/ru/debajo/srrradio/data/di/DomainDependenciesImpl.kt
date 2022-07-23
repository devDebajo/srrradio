package ru.debajo.srrradio.data.di

import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.UserStationUseCase
import ru.debajo.srrradio.domain.di.DomainDependencies
import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository
import ru.debajo.srrradio.domain.repository.SearchStationsRepository
import ru.debajo.srrradio.domain.repository.TracksCollectionRepository

object DomainDependenciesImpl : DomainDependencies {
    override val searchStationsRepository: SearchStationsRepository
        get() = DataApiHolder.get().searchStationsRepository

    override val favoriteStationsRepository: FavoriteStationsRepository
        get() = DataApiHolder.get().favoriteStationsRepository

    override val lastStationUseCase: LastStationUseCase
        get() = DataApiHolder.get().lastStationUseCase

    override val loadPlaylistUseCase: LoadPlaylistUseCase
        get() = DataApiHolder.get().loadPlaylistUseCase

    override val userStationUseCase: UserStationUseCase
        get() = DataApiHolder.get().userStationUseCase

    override val tracksCollectionRepository: TracksCollectionRepository
        get() = DataApiHolder.get().tracksCollectionRepository
}
