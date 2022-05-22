package ru.debajo.srrradio.domain.di

import ru.debajo.srrradio.common.di.ModuleApi
import ru.debajo.srrradio.domain.SearchStationsUseCase

interface DomainApi : ModuleApi {
    fun searchStationsUseCase(): SearchStationsUseCase
}
