package ru.debajo.srrradio.data.di

import android.content.SharedPreferences
import kotlinx.serialization.json.Json
import ru.debajo.srrradio.common.di.ModuleApi
import ru.debajo.srrradio.data.service.ApiHostDiscovery
import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

interface DataApi : ModuleApi {
    fun searchStationsRepository(): SearchStationsRepository
    fun sharedPreferences(): SharedPreferences
    fun apiHostDiscovery(): ApiHostDiscovery
    fun lastStationUseCase(): LastStationUseCase
    fun loadPlaylistUseCase(): LoadPlaylistUseCase
    fun json(): Json
}
