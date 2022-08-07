package ru.debajo.srrradio.domain.di

import ru.debajo.srrradio.common.di.ModuleApi
import ru.debajo.srrradio.domain.FavoriteStationsStateUseCase
import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.ParseM3uUseCase
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.TracksCollectionUseCase
import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCase
import ru.debajo.srrradio.domain.UserLocationUseCase
import ru.debajo.srrradio.domain.UserStationUseCase

interface DomainApi : ModuleApi {
    val searchStationsUseCase: SearchStationsUseCase
    val lastStationUseCase: LastStationUseCase
    val loadPlaylistUseCase: LoadPlaylistUseCase
    val userStationUseCase: UserStationUseCase
    val updateFavoriteStationStateUseCase: UpdateFavoriteStationStateUseCase
    val favoriteStationsStateUseCase: FavoriteStationsStateUseCase
    val tracksCollectionUseCase: TracksCollectionUseCase
    val parseM3uUseCase: ParseM3uUseCase
    val userLocationUseCase: UserLocationUseCase
}
