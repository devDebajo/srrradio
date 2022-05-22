package ru.debajo.srrradio.domain.di

import ru.debajo.srrradio.common.di.ModuleDependencies
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

interface DomainDependencies : ModuleDependencies {
    val searchStationsRepository: SearchStationsRepository
}
