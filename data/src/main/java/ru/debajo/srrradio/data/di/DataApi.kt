package ru.debajo.srrradio.data.di

import android.content.SharedPreferences
import kotlinx.serialization.json.Json
import ru.debajo.srrradio.common.di.ModuleApi
import ru.debajo.srrradio.data.service.ApiHostDiscovery
import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.ParseM3uUseCase
import ru.debajo.srrradio.domain.UserStationUseCase
import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository
import ru.debajo.srrradio.domain.repository.SearchStationsRepository
import ru.debajo.srrradio.domain.repository.SyncRepository
import ru.debajo.srrradio.domain.repository.TracksCollectionRepository

interface DataApi : ModuleApi {
    val searchStationsRepository: SearchStationsRepository
    val favoriteStationsRepository: FavoriteStationsRepository
    val sharedPreferences: SharedPreferences
    val apiHostDiscovery: ApiHostDiscovery
    val lastStationUseCase: LastStationUseCase
    val loadPlaylistUseCase: LoadPlaylistUseCase
    val userStationUseCase: UserStationUseCase
    val parseM3uUseCase: ParseM3uUseCase
    val tracksCollectionRepository: TracksCollectionRepository
    val syncRepository: SyncRepository
}

internal interface DataApiInternal : DataApi {
    val json: Json
}
