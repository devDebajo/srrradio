package ru.debajo.srrradio.domain.di

import ru.debajo.srrradio.common.di.ModuleApi
import ru.debajo.srrradio.domain.*

interface DomainApi : ModuleApi {
    val searchStationsUseCase: SearchStationsUseCase
    val lastStationUseCase: LastStationUseCase
    val loadPlaylistUseCase: LoadPlaylistUseCase
    val updateFavoriteStationStateUseCase: UpdateFavoriteStationStateUseCase
    val favoriteStationsStateUseCase: FavoriteStationsStateUseCase
}
