package ru.debajo.srrradio.data.di

import ru.debajo.srrradio.common.di.ModuleApi
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

internal interface DataApi : ModuleApi {
    fun searchStationsRepository(): SearchStationsRepository
}
