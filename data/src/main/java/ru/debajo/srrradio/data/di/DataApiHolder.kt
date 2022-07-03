package ru.debajo.srrradio.data.di

import ru.debajo.srrradio.common.di.ModuleApiHolder

object DataApiHolder : ModuleApiHolder<DataApi>() {

    internal val internalApi: DataApiInternal get() = get() as DataApiInternal

    override fun buildApi(): DataApi = DataModule.Impl(DataDependencies.Impl)
}
