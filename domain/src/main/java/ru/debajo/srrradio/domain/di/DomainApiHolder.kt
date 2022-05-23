package ru.debajo.srrradio.domain.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.debajo.srrradio.common.di.ExtDependenciesModuleApiHolder
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

object DomainApiHolder : ExtDependenciesModuleApiHolder<DomainApi, DomainDependencies>() {
    override val koinModules: List<Module> = listOf(
        module {
            single<SearchStationsUseCase> { get<SearchStationsRepository>() }
        }
    )
}
