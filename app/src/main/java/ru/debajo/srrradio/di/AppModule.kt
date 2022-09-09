package ru.debajo.srrradio.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.debajo.srrradio.auth.AuthManager
import ru.debajo.srrradio.error.SendingErrorsManager
import ru.debajo.srrradio.error.SendingErrorsPreference
import ru.debajo.srrradio.icon.AppIconManager
import ru.debajo.srrradio.icon.DynamicIconPreference
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.media.MediaSessionController
import ru.debajo.srrradio.media.RadioPlayer
import ru.debajo.srrradio.media.StationCoverLoader
import ru.debajo.srrradio.sync.AppStateSnapshotExtractor
import ru.debajo.srrradio.sync.AppStateSnapshotMerger
import ru.debajo.srrradio.sync.AppSynchronizer
import ru.debajo.srrradio.ui.host.add.AddCustomStationCommandResultReduktor
import ru.debajo.srrradio.ui.host.add.AddCustomStationReduktor
import ru.debajo.srrradio.ui.host.add.AddCustomStationViewModel
import ru.debajo.srrradio.ui.host.collection.CollectionViewModel
import ru.debajo.srrradio.ui.host.main.list.StationsListViewModel
import ru.debajo.srrradio.ui.host.main.list.reduktor.StationsListCommandResultReduktor
import ru.debajo.srrradio.ui.host.main.list.reduktor.StationsListReduktor
import ru.debajo.srrradio.ui.host.main.player.PlayerBottomSheetViewModel
import ru.debajo.srrradio.ui.host.main.player.reduktor.PlayerBottomSheetCommandResultReduktor
import ru.debajo.srrradio.ui.host.main.player.reduktor.PlayerBottomSheetReduktor
import ru.debajo.srrradio.ui.host.main.playlist.DefaultPlaylistViewModel
import ru.debajo.srrradio.ui.host.main.settings.SettingsViewModel
import ru.debajo.srrradio.ui.host.main.timer.SleepTimer
import ru.debajo.srrradio.ui.host.main.timer.SleepTimerViewModel
import ru.debajo.srrradio.ui.processor.AddFavoriteStationProcessor
import ru.debajo.srrradio.ui.processor.AddTrackToCollectionProcessor
import ru.debajo.srrradio.ui.processor.ListenFavoriteStationsProcessor
import ru.debajo.srrradio.ui.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.processor.NewPlayCommandProcessor
import ru.debajo.srrradio.ui.processor.PopularStationsProcessor
import ru.debajo.srrradio.ui.processor.SaveCustomStationProcessor
import ru.debajo.srrradio.ui.processor.SearchStationsCommandProcessor
import ru.debajo.srrradio.ui.processor.SleepTimerListenerProcessor
import ru.debajo.srrradio.ui.processor.TrackCollectionListener
import ru.debajo.srrradio.ui.processor.interactor.LoadM3uInteractor
import ru.debajo.srrradio.ui.processor.interactor.UserStationsInteractor
import ru.debajo.srrradio.ui.theme.SrrradioThemeManager
import ru.debajo.srrradio.ui.theme.SrrradioThemePreference

val AppModule: Module = module {
    single { FirebaseCrashlytics.getInstance() }
    single { FirebaseAuth.getInstance() }
    
    single { AuthManager(get(), get()) }
    factory { AppStateSnapshotMerger() }
    single { AppSynchronizer(get(), get(), get(), get()) }
    factory { AppStateSnapshotExtractor(get(), get(), get(), get(), get(), get(), get()) }

    single { StationCoverLoader(get()) }
    single { SleepTimer() }
    factory { SendingErrorsManager(get(), get()) }

    single { SrrradioThemeManager(get()) }
    factory { AppIconManager(get(), get()) }

    single { MediaSessionController(get()) }
    single {
        val player = RadioPlayer(get(), get(), get())
        MediaController(player, get(), get(), get())
    }

    factory { PlayerBottomSheetViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { SleepTimerViewModel(get()) }
    factory { SettingsViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { AddCustomStationViewModel(get(), get(), get(), get()) }
    factory { CollectionViewModel(get()) }
    factory { DefaultPlaylistViewModel(get(), get(), get(), get(), get(), get()) }
    factory { StationsListViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }

    factory { SendingErrorsPreference(get()) }
    factory { SrrradioThemePreference(get()) }
    factory { DynamicIconPreference(get()) }

    factory { UserStationsInteractor(get(), get()) }
    factory { LoadM3uInteractor(get(), get(), get(), get()) }

    factory { StationsListReduktor(get()) }
    factory { StationsListCommandResultReduktor(get()) }
    factory { PlayerBottomSheetReduktor(get()) }
    factory { PlayerBottomSheetCommandResultReduktor() }
    factory { AddCustomStationReduktor() }
    factory { AddCustomStationCommandResultReduktor() }

    factory { SearchStationsCommandProcessor(get()) }
    factory { NewPlayCommandProcessor(get()) }
    factory { MediaStateListenerCommandProcessor(get()) }
    factory { ListenFavoriteStationsProcessor(get()) }
    factory { AddFavoriteStationProcessor(get()) }
    factory { TrackCollectionListener(get()) }
    factory { PopularStationsProcessor(get()) }
    factory { SleepTimerListenerProcessor(get()) }
    factory { AddTrackToCollectionProcessor(get()) }
    factory { SaveCustomStationProcessor(get()) }
}
