package ru.debajo.srrradio.domain.di

import ru.debajo.srrradio.common.di.ModuleApi
import ru.debajo.srrradio.domain.FavoriteStationsStateUseCase
import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCase

interface DomainApi : ModuleApi {
    val searchStationsUseCase: SearchStationsUseCase
    val lastStationUseCase: LastStationUseCase
    val loadPlaylistUseCase: LoadPlaylistUseCase
    val updateFavoriteStationStateUseCase: UpdateFavoriteStationStateUseCase
    val favoriteStationsStateUseCase: FavoriteStationsStateUseCase
}
