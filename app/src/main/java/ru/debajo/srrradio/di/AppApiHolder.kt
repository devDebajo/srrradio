package ru.debajo.srrradio.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.debajo.srrradio.RadioPlayer
import ru.debajo.srrradio.common.di.ModuleApiHolder
import ru.debajo.srrradio.ui.list.StationsListViewModel
import ru.debajo.srrradio.ui.list.reduktor.StationsListCommandResultReduktor
import ru.debajo.srrradio.ui.list.reduktor.StationsListReduktor
import ru.debajo.srrradio.ui.list.reduktor.processor.PlayerStateListenerCommandProcessor
import ru.debajo.srrradio.ui.list.reduktor.processor.SearchStationsCommandProcessor

internal object AppApiHolder : ModuleApiHolder<AppApi, AppDependencies>() {
    override val koinModules: List<Module> = listOf(
        module {
            single { StationsListReduktor(get()) }
            single { StationsListCommandResultReduktor() }
            single { SearchStationsCommandProcessor(get()) }
            single { PlayerStateListenerCommandProcessor(get()) }
            factory { StationsListViewModel(get(), get(), get(), get()) }

            single { RadioPlayer(get()) }
        }
    )
    override val dependencies: AppDependencies = AppDependencies.Impl
}
