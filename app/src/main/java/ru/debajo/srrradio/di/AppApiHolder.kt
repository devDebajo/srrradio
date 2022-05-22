package ru.debajo.srrradio.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.debajo.srrradio.common.di.ModuleApiHolder
import ru.debajo.srrradio.ui.list.StationsListViewModel
import ru.debajo.srrradio.ui.list.reduktor.StationsListCommandResultReduktor
import ru.debajo.srrradio.ui.list.reduktor.StationsListReduktor
import ru.debajo.srrradio.ui.list.reduktor.processor.SearchStationsCommandProcessor

internal object AppApiHolder : ModuleApiHolder<AppApi, AppDependencies>() {
    override val koinModules: List<Module> = listOf(
        module {
            single { StationsListReduktor() }
            single { StationsListCommandResultReduktor() }
            single { SearchStationsCommandProcessor(dependencies.searchStationsUseCase) }
            factory { StationsListViewModel(get(), get(), get()) }
        }
    )
    override val dependencies: AppDependencies = AppDependencies.Impl
}
