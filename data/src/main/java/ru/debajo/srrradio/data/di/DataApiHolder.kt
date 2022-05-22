package ru.debajo.srrradio.data.di

import org.koin.core.module.Module
import ru.debajo.srrradio.common.di.PureModuleApiHolder

internal object DataApiHolder : PureModuleApiHolder<DataApi>() {
    override val koinModules: List<Module> = listOf(DataDiModule)
}
