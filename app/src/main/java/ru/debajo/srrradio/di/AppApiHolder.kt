package ru.debajo.srrradio.di

import ru.debajo.srrradio.common.di.ExtDependenciesModuleApiHolder

internal object AppApiHolder : ExtDependenciesModuleApiHolder<AppApi, AppDependencies>() {
    override fun buildApi(dependencies: AppDependencies): AppApi = AppModule.Impl(dependencies)
}
