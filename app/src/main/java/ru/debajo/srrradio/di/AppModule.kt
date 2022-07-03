package ru.debajo.srrradio.di

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import ru.debajo.srrradio.MediaController
import ru.debajo.srrradio.RadioPlayer
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.ui.list.StationsListViewModel
import ru.debajo.srrradio.ui.list.reduktor.StationsListCommandResultReduktor
import ru.debajo.srrradio.ui.list.reduktor.StationsListReduktor
import ru.debajo.srrradio.ui.list.reduktor.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.list.reduktor.processor.NewPlayCommandProcessor
import ru.debajo.srrradio.ui.list.reduktor.processor.SearchStationsCommandProcessor
import ru.debajo.srrradio.ui.player.PlayerBottomSheetCommandResultReduktor
import ru.debajo.srrradio.ui.player.PlayerBottomSheetReduktor
import ru.debajo.srrradio.ui.player.PlayerBottomSheetViewModel

internal interface AppModule : AppApi {
    fun provideStationsListViewModel(
        reduktor: StationsListReduktor,
        commandResultReduktor: StationsListCommandResultReduktor,
        searchStationsCommandProcessor: SearchStationsCommandProcessor,
        mediaStateListener: MediaStateListenerCommandProcessor,
        newPlayCommandProcessor: NewPlayCommandProcessor,
    ): StationsListViewModel {
        return StationsListViewModel(
            reduktor = reduktor,
            commandResultReduktor = commandResultReduktor,
            searchStationsCommandProcessor = searchStationsCommandProcessor,
            mediaStateListener = mediaStateListener,
            newPlayCommandProcessor = newPlayCommandProcessor
        )
    }

    fun provideStationsListReduktor(): StationsListReduktor = StationsListReduktor()

    fun provideStationsListCommandResultReduktor(): StationsListCommandResultReduktor = StationsListCommandResultReduktor()

    fun provideSearchStationsCommandProcessor(searchStationsUseCase: SearchStationsUseCase): SearchStationsCommandProcessor {
        return SearchStationsCommandProcessor(searchStationsUseCase)
    }

    fun provideMediaStateListenerCommandProcessor(mediaController: MediaController): MediaStateListenerCommandProcessor {
        return MediaStateListenerCommandProcessor(mediaController)
    }

    fun provideRadioPlayer(context: Context, coroutineScope: CoroutineScope): RadioPlayer {
        return RadioPlayer(context = context, coroutineScope = coroutineScope)
    }

    fun provideNewPlayCommandProcessor(mediaController: MediaController): NewPlayCommandProcessor = NewPlayCommandProcessor(mediaController)

    fun providePlayerBottomSheetViewModel(
        reduktor: PlayerBottomSheetReduktor,
        commandResultReduktor: PlayerBottomSheetCommandResultReduktor,
        mediaStateListenerCommandProcessor: MediaStateListenerCommandProcessor,
    ): PlayerBottomSheetViewModel {
        return PlayerBottomSheetViewModel(
            reduktor = reduktor,
            commandResultReduktor = commandResultReduktor,
            mediaStateListenerCommandProcessor = mediaStateListenerCommandProcessor,
        )
    }

    fun provideCommandResultReduktor(): PlayerBottomSheetCommandResultReduktor = PlayerBottomSheetCommandResultReduktor()

    fun providePlayerBottomSheetReduktor(mediaController: MediaController): PlayerBottomSheetReduktor = PlayerBottomSheetReduktor(mediaController)

    class Impl(private val dependencies: AppDependencies) : AppModule {

        override val coroutineScope: CoroutineScope
            get() = dependencies.applicationCoroutineScope

        override val stationsListViewModel: StationsListViewModel
            get() = provideStationsListViewModel(
                reduktor = provideStationsListReduktor(),
                commandResultReduktor = provideStationsListCommandResultReduktor(),
                searchStationsCommandProcessor = provideSearchStationsCommandProcessor(dependencies.searchStationsUseCase),
                mediaStateListener = provideMediaStateListenerCommandProcessor(mediaController),
                newPlayCommandProcessor = provideNewPlayCommandProcessor(mediaController)
            )

        override val playerBottomSheetViewModel: PlayerBottomSheetViewModel
            get() = providePlayerBottomSheetViewModel(
                reduktor = providePlayerBottomSheetReduktor(mediaController),
                commandResultReduktor = provideCommandResultReduktor(),
                mediaStateListenerCommandProcessor = provideMediaStateListenerCommandProcessor(mediaController),
            )

        override val mediaController: MediaController by lazy {
            MediaController(
                player = provideRadioPlayer(
                    context = dependencies.context,
                    coroutineScope = dependencies.applicationCoroutineScope
                ),
                lastStationUseCase = dependencies.lastStationUseCase,
                loadPlaylistUseCase = dependencies.loadPlaylistUseCase,
                coroutineScope = dependencies.applicationCoroutineScope,
            )
        }
    }
}
