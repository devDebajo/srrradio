package ru.debajo.srrradio.di

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import ru.debajo.srrradio.common.di.CommonApiHolder
import ru.debajo.srrradio.data.di.DataApiHolder
import ru.debajo.srrradio.domain.FavoriteStationsStateUseCase
import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.TracksCollectionUseCase
import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCase
import ru.debajo.srrradio.domain.UserStationUseCase
import ru.debajo.srrradio.domain.di.DomainApiHolder

internal interface AppDependencies {

    val searchStationsUseCase: SearchStationsUseCase
    val context: Context
    val applicationCoroutineScope: CoroutineScope
    val lastStationUseCase: LastStationUseCase
    val loadPlaylistUseCase: LoadPlaylistUseCase
    val userStationUseCase: UserStationUseCase
    val updateFavoriteStationStateUseCase: UpdateFavoriteStationStateUseCase
    val favoriteStationsStateUseCase: FavoriteStationsStateUseCase
    val tracksCollectionUseCase: TracksCollectionUseCase
    val sharedPreferences: SharedPreferences

    class Impl(override val applicationCoroutineScope: CoroutineScope) : AppDependencies {

        override val searchStationsUseCase: SearchStationsUseCase
            get() = DomainApiHolder.get().searchStationsUseCase

        override val context: Context
            get() = CommonApiHolder.get().context

        override val lastStationUseCase: LastStationUseCase
            get() = DomainApiHolder.get().lastStationUseCase

        override val loadPlaylistUseCase: LoadPlaylistUseCase
            get() = DomainApiHolder.get().loadPlaylistUseCase

        override val userStationUseCase: UserStationUseCase
            get() = DomainApiHolder.get().userStationUseCase

        override val updateFavoriteStationStateUseCase: UpdateFavoriteStationStateUseCase
            get() = DomainApiHolder.get().updateFavoriteStationStateUseCase

        override val favoriteStationsStateUseCase: FavoriteStationsStateUseCase
            get() = DomainApiHolder.get().favoriteStationsStateUseCase

        override val tracksCollectionUseCase: TracksCollectionUseCase
            get() = DomainApiHolder.get().tracksCollectionUseCase

        override val sharedPreferences: SharedPreferences
            get() = DataApiHolder.get().sharedPreferences
    }
}
