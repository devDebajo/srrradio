package ru.debajo.srrradio.di

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import ru.debajo.srrradio.domain.FavoriteStationsStateUseCase
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCase
import ru.debajo.srrradio.domain.UserStationUseCase
import ru.debajo.srrradio.domain.UserStationsInteractor
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.media.RadioPlayer
import ru.debajo.srrradio.media.StationCoverLoader
import ru.debajo.srrradio.ui.host.add.AddCustomStationCommandResultReduktor
import ru.debajo.srrradio.ui.host.add.AddCustomStationReduktor
import ru.debajo.srrradio.ui.host.add.AddCustomStationViewModel
import ru.debajo.srrradio.ui.host.main.list.StationsListViewModel
import ru.debajo.srrradio.ui.host.main.list.reduktor.StationsListCommandResultReduktor
import ru.debajo.srrradio.ui.host.main.list.reduktor.StationsListReduktor
import ru.debajo.srrradio.ui.host.main.player.PlayerBottomSheetViewModel
import ru.debajo.srrradio.ui.host.main.player.reduktor.PlayerBottomSheetCommandResultReduktor
import ru.debajo.srrradio.ui.host.main.player.reduktor.PlayerBottomSheetReduktor
import ru.debajo.srrradio.ui.host.main.settings.SettingsViewModel
import ru.debajo.srrradio.ui.host.main.timer.SleepTimer
import ru.debajo.srrradio.ui.host.main.timer.SleepTimerViewModel
import ru.debajo.srrradio.ui.processor.AddFavoriteStationProcessor
import ru.debajo.srrradio.ui.processor.ListenFavoriteStationsProcessor
import ru.debajo.srrradio.ui.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.processor.NewPlayCommandProcessor
import ru.debajo.srrradio.ui.processor.SaveCustomStationProcessor
import ru.debajo.srrradio.ui.processor.SearchStationsCommandProcessor
import ru.debajo.srrradio.ui.processor.SleepTimerListenerProcessor
import ru.debajo.srrradio.ui.theme.SrrradioThemeManager

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

    fun provideAddCustomStationViewModel(
        reduktor: AddCustomStationReduktor,
        commandResultReduktor: AddCustomStationCommandResultReduktor,
        searchStationsCommandProcessor: SearchStationsCommandProcessor,
        saveCustomStationProcessor: SaveCustomStationProcessor,
    ): AddCustomStationViewModel = AddCustomStationViewModel(
        reduktor = reduktor,
        commandResultReduktor = commandResultReduktor,
        searchStationsCommandProcessor = searchStationsCommandProcessor,
        saveCustomStationProcessor = saveCustomStationProcessor,
    )

    fun provideUserStationsInteractor(
        userStationUseCase: UserStationUseCase,
        updateFavoriteStationStateUseCase: UpdateFavoriteStationStateUseCase
    ): UserStationsInteractor = UserStationsInteractor(userStationUseCase, updateFavoriteStationStateUseCase)

    fun provideSaveCustomStationProcessor(userStationsInteractor: UserStationsInteractor): SaveCustomStationProcessor {
        return SaveCustomStationProcessor(userStationsInteractor)
    }

    fun provideAddCustomStationCommandResultReduktor(): AddCustomStationCommandResultReduktor {
        return AddCustomStationCommandResultReduktor()
    }

    fun provideAddCustomStationReduktor(): AddCustomStationReduktor {
        return AddCustomStationReduktor()
    }

    fun provideSettingsViewModel(themeManager: SrrradioThemeManager): SettingsViewModel = SettingsViewModel(themeManager)

    fun provideSrrradioThemeManager(sharedPreferences: SharedPreferences): SrrradioThemeManager = SrrradioThemeManager(sharedPreferences)

    class Impl(private val dependencies: AppDependencies) : AppModule {

        private val searchStationsCommandProcessor: SearchStationsCommandProcessor
            get() = provideSearchStationsCommandProcessor(dependencies.searchStationsUseCase)

        override val sleepTimer: SleepTimer by lazy { provideSleepTimer() }

        override val coroutineScope: CoroutineScope
            get() = dependencies.applicationCoroutineScope

        override val stationsListViewModel: StationsListViewModel
            get() = provideStationsListViewModel(
                reduktor = provideStationsListReduktor(dependencies.context),
                commandResultReduktor = provideStationsListCommandResultReduktor(dependencies.context),
                searchStationsCommandProcessor = searchStationsCommandProcessor,
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

        override val addCustomStationViewModel: AddCustomStationViewModel
            get() = provideAddCustomStationViewModel(
                reduktor = provideAddCustomStationReduktor(),
                commandResultReduktor = provideAddCustomStationCommandResultReduktor(),
                searchStationsCommandProcessor = searchStationsCommandProcessor,
                saveCustomStationProcessor = provideSaveCustomStationProcessor(
                    userStationsInteractor = provideUserStationsInteractor(
                        userStationUseCase = dependencies.userStationUseCase,
                        updateFavoriteStationStateUseCase = dependencies.updateFavoriteStationStateUseCase
                    )
                )
            )
        override val settingsViewModel: SettingsViewModel
            get() = provideSettingsViewModel(themeManager)


        override val themeManager: SrrradioThemeManager by lazy {
            provideSrrradioThemeManager(dependencies.sharedPreferences)
        }


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
