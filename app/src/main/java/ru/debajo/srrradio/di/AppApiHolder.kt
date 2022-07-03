package ru.debajo.srrradio.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.debajo.srrradio.MediaActions
import ru.debajo.srrradio.MediaController
import ru.debajo.srrradio.RadioPlayer
import ru.debajo.srrradio.common.di.ExtDependenciesModuleApiHolder
import ru.debajo.srrradio.ui.list.StationsListViewModel
import ru.debajo.srrradio.ui.list.reduktor.StationsListCommandResultReduktor
import ru.debajo.srrradio.ui.list.reduktor.StationsListReduktor
import ru.debajo.srrradio.ui.list.reduktor.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.list.reduktor.processor.NewPlayCommandProcessor
import ru.debajo.srrradio.ui.list.reduktor.processor.SearchStationsCommandProcessor
import ru.debajo.srrradio.ui.player.PlayerBottomSheetCommandResultReduktor
import ru.debajo.srrradio.ui.player.PlayerBottomSheetReduktor
import ru.debajo.srrradio.ui.player.PlayerBottomSheetViewModel

internal object AppApiHolder : ExtDependenciesModuleApiHolder<AppApi, AppDependencies>() {
    override val koinModules: List<Module> = listOf(
        module {
            factory { SearchStationsCommandProcessor(get()) }
            factory { MediaStateListenerCommandProcessor(get()) }
            factory { NewPlayCommandProcessor(get()) }
        },
        module {
            single {
                MediaController(
                    player = RadioPlayer(get(), get()),
                    lastStationUseCase = get(),
                    loadPlaylistUseCase = get(),
                    coroutineScope = get()
                )
            }
            single<MediaActions> { get<MediaController>() }
        },
        module {
            factory { StationsListReduktor() }
            factory { StationsListCommandResultReduktor() }
            factory { StationsListViewModel(get(), get(), get(), get(), get()) }
        },
        module {
            factory { PlayerBottomSheetReduktor(get()) }
            factory { PlayerBottomSheetCommandResultReduktor() }
            factory { PlayerBottomSheetViewModel(get(), get(), get()) }
        }
    )
}
