package ru.debajo.srrradio.data.di

import android.content.Context
import android.location.LocationManager
import ru.debajo.srrradio.common.di.CommonApiHolder
import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.ParseM3uUseCase
import ru.debajo.srrradio.domain.SyncUseCase
import ru.debajo.srrradio.domain.UserStationUseCase
import ru.debajo.srrradio.domain.di.DomainDependencies
import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository
import ru.debajo.srrradio.domain.repository.SearchStationsRepository
import ru.debajo.srrradio.domain.repository.TracksCollectionRepository

object DomainDependenciesImpl : DomainDependencies {
    override val context: Context
        get() = CommonApiHolder.get().context

    override val locationManager: LocationManager
        get() = CommonApiHolder.get().locationManager

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

    override val parseM3uUseCase: ParseM3uUseCase
        get() = DataApiHolder.get().parseM3uUseCase

    override val syncUseCase: SyncUseCase
        get() = DataApiHolder.get().syncRepository
}
