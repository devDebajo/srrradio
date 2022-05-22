package ru.debajo.srrradio.data.di

import ru.debajo.srrradio.domain.di.DomainDependencies
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

object DomainDependenciesImpl : DomainDependencies {
    override val searchStationsRepository: SearchStationsRepository
        get() = DataApiHolder.get().searchStationsRepository()
}
