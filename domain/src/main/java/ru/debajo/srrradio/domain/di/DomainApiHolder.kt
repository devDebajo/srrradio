package ru.debajo.srrradio.domain.di

import ru.debajo.srrradio.common.di.ExtDependenciesModuleApiHolder

object DomainApiHolder : ExtDependenciesModuleApiHolder<DomainApi, DomainDependencies>() {
    override fun buildApi(dependencies: DomainDependencies): DomainApi = DomainModule.Impl(dependencies)
}
