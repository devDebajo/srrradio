package ru.debajo.srrradio.di

import ru.debajo.srrradio.common.di.ModuleApiHolder

internal object AppApiHolder : ModuleApiHolder<AppApi>() {
    override fun buildApi(): AppApi = AppModule.Impl(AppDependencies.Impl)
}
