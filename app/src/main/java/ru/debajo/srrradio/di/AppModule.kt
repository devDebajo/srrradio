package ru.debajo.srrradio.di

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import ru.debajo.srrradio.MediaController
import ru.debajo.srrradio.RadioPlayer
import ru.debajo.srrradio.StationCoverLoader
import ru.debajo.srrradio.domain.FavoriteStationsStateUseCase
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCase
import ru.debajo.srrradio.ui.list.StationsListViewModel
import ru.debajo.srrradio.ui.list.reduktor.StationsListCommandResultReduktor
import ru.debajo.srrradio.ui.list.reduktor.StationsListReduktor
import ru.debajo.srrradio.ui.player.PlayerBottomSheetViewModel
import ru.debajo.srrradio.ui.player.reduktor.PlayerBottomSheetCommandResultReduktor
import ru.debajo.srrradio.ui.player.reduktor.PlayerBottomSheetReduktor
import ru.debajo.srrradio.ui.processor.AddFavoriteStationProcessor
import ru.debajo.srrradio.ui.processor.ListenFavoriteStationsProcessor
import ru.debajo.srrradio.ui.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.processor.NewPlayCommandProcessor
import ru.debajo.srrradio.ui.processor.SearchStationsCommandProcessor
import ru.debajo.srrradio.ui.processor.SleepTimerListenerProcessor
import ru.debajo.srrradio.ui.timer.SleepTimer
import ru.debajo.srrradio.ui.timer.SleepTimerViewModel

internal interface AppModule : AppApi {
    fun provideStationsListViewModel(
        reduktor: StationsListReduktor,
        commandResultReduktor: StationsListCommandResultReduktor,
        searchStationsCommandProcessor: SearchStationsCommandProcessor,
        mediaStateListener: MediaStateListenerCommandProcessor,
        newPlayCommandProcessor: NewPlayCommandProcessor,
        listenFavoriteStationsProcessor: ListenFavoriteStationsProcessor,
        addFavoriteStationProcessor: AddFavoriteStationProcessor,
    ): StationsListViewModel {
        return StationsListViewModel(
            reduktor = reduktor,
            commandResultReduktor = commandResultReduktor,
            searchStationsCommandProcessor = searchStationsCommandProcessor,
            mediaStateListener = mediaStateListener,
            newPlayCommandProcessor = newPlayCommandProcessor,
            listenFavoriteStationsProcessor = listenFavoriteStationsProcessor,
            addFavoriteStationProcessor = addFavoriteStationProcessor,
        )
    }

    fun provideStationsListReduktor(context: Context): StationsListReduktor = StationsListReduktor(context)

    fun provideStationsListCommandResultReduktor(context: Context): StationsListCommandResultReduktor = StationsListCommandResultReduktor(context)

    fun provideSearchStationsCommandProcessor(searchStationsUseCase: SearchStationsUseCase): SearchStationsCommandProcessor {
        return SearchStationsCommandProcessor(searchStationsUseCase)
    }

    fun provideMediaStateListenerCommandProcessor(mediaController: MediaController): MediaStateListenerCommandProcessor {
        return MediaStateListenerCommandProcessor(mediaController)
    }

    fun provideRadioPlayer(
        context: Context,
        stationCoverLoader: StationCoverLoader,
        coroutineScope: CoroutineScope
    ): RadioPlayer {
        return RadioPlayer(
            context = context,
            stationCoverLoader = stationCoverLoader,
            coroutineScope = coroutineScope
        )
    }

    fun provideNewPlayCommandProcessor(mediaController: MediaController): NewPlayCommandProcessor = NewPlayCommandProcessor(mediaController)

    fun providePlayerBottomSheetViewModel(
        reduktor: PlayerBottomSheetReduktor,
        commandResultReduktor: PlayerBottomSheetCommandResultReduktor,
        mediaStateListenerCommandProcessor: MediaStateListenerCommandProcessor,
        addFavoriteStationProcessor: AddFavoriteStationProcessor,
        listenFavoriteStationsProcessor: ListenFavoriteStationsProcessor,
        sleepTimerListenerProcessor: SleepTimerListenerProcessor
    ): PlayerBottomSheetViewModel {
        return PlayerBottomSheetViewModel(
            reduktor = reduktor,
            commandResultReduktor = commandResultReduktor,
            mediaStateListenerCommandProcessor = mediaStateListenerCommandProcessor,
            addFavoriteStationProcessor = addFavoriteStationProcessor,
            listenFavoriteStationsProcessor = listenFavoriteStationsProcessor,
            sleepTimerListenerProcessor = sleepTimerListenerProcessor,
        )
    }

    fun provideCommandResultReduktor(): PlayerBottomSheetCommandResultReduktor = PlayerBottomSheetCommandResultReduktor()

    fun providePlayerBottomSheetReduktor(mediaController: MediaController): PlayerBottomSheetReduktor = PlayerBottomSheetReduktor(mediaController)

    fun provideAddFavoriteStationProcessor(useCase: UpdateFavoriteStationStateUseCase): AddFavoriteStationProcessor {
        return AddFavoriteStationProcessor(useCase)
    }

    fun provideListenFavoriteStationsProcessor(useCase: FavoriteStationsStateUseCase): ListenFavoriteStationsProcessor {
        return ListenFavoriteStationsProcessor(useCase)
    }

    fun provideStationCoverLoader(context: Context): StationCoverLoader {
        return StationCoverLoader(context)
    }

    fun provideSleepTimerViewModel(sleepTimer: SleepTimer): SleepTimerViewModel {
        return SleepTimerViewModel(sleepTimer)
    }

    fun provideSleepTimer(): SleepTimer = SleepTimer()

    fun provideSleepTimerListenerProcessor(sleepTimer: SleepTimer): SleepTimerListenerProcessor {
        return SleepTimerListenerProcessor(sleepTimer)
    }

    class Impl(private val dependencies: AppDependencies) : AppModule {

        override val sleepTimer: SleepTimer by lazy { provideSleepTimer() }

        override val coroutineScope: CoroutineScope
            get() = dependencies.applicationCoroutineScope

        override val stationsListViewModel: StationsListViewModel
            get() = provideStationsListViewModel(
                reduktor = provideStationsListReduktor(dependencies.context),
                commandResultReduktor = provideStationsListCommandResultReduktor(dependencies.context),
                searchStationsCommandProcessor = provideSearchStationsCommandProcessor(dependencies.searchStationsUseCase),
                mediaStateListener = provideMediaStateListenerCommandProcessor(mediaController),
                newPlayCommandProcessor = provideNewPlayCommandProcessor(mediaController),
                listenFavoriteStationsProcessor = provideListenFavoriteStationsProcessor(dependencies.favoriteStationsStateUseCase),
                addFavoriteStationProcessor = provideAddFavoriteStationProcessor(dependencies.updateFavoriteStationStateUseCase),
            )

        override val playerBottomSheetViewModel: PlayerBottomSheetViewModel
            get() = providePlayerBottomSheetViewModel(
                reduktor = providePlayerBottomSheetReduktor(mediaController),
                commandResultReduktor = provideCommandResultReduktor(),
                mediaStateListenerCommandProcessor = provideMediaStateListenerCommandProcessor(mediaController),
                addFavoriteStationProcessor = provideAddFavoriteStationProcessor(dependencies.updateFavoriteStationStateUseCase),
                listenFavoriteStationsProcessor = provideListenFavoriteStationsProcessor(dependencies.favoriteStationsStateUseCase),
                sleepTimerListenerProcessor = provideSleepTimerListenerProcessor(sleepTimer),
            )

        override val sleepTimerViewModel: SleepTimerViewModel
            get() = provideSleepTimerViewModel(sleepTimer)

        override val mediaController: MediaController by lazy {
            MediaController(
                player = provideRadioPlayer(
                    context = dependencies.context,
                    stationCoverLoader = provideStationCoverLoader(dependencies.context),
                    coroutineScope = dependencies.applicationCoroutineScope
                ),
                lastStationUseCase = dependencies.lastStationUseCase,
                loadPlaylistUseCase = dependencies.loadPlaylistUseCase,
                coroutineScope = dependencies.applicationCoroutineScope,
            )
        }
    }
}
