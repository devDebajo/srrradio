package ru.debajo.srrradio.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.debajo.srrradio.auth.AuthManagerProvider
import ru.debajo.srrradio.auth.F
import ru.debajo.srrradio.bluetooth.BluetoothAutoplayPreference
import ru.debajo.srrradio.error.SendingErrorsManager
import ru.debajo.srrradio.error.SendingErrorsPreference
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.media.MediaSessionController
import ru.debajo.srrradio.media.PlayerVolumePreference
import ru.debajo.srrradio.media.RadioEqualizerPreference
import ru.debajo.srrradio.media.RadioPlayer
import ru.debajo.srrradio.media.StationCoverLoader
import ru.debajo.srrradio.rate.GoogleServicesUtils
import ru.debajo.srrradio.rate.HostActivityCreateCountPreference
import ru.debajo.srrradio.rate.InitialAutoplayPreference
import ru.debajo.srrradio.rate.RateAppManager
import ru.debajo.srrradio.rate.RateAppStatePreference
import ru.debajo.srrradio.service.PlaybackBroadcastReceiver
import ru.debajo.srrradio.service.SrrradioNotificationManager
import ru.debajo.srrradio.sync.AppStateSnapshotExtractor
import ru.debajo.srrradio.sync.AppStateSnapshotMerger
import ru.debajo.srrradio.sync.AppSynchronizer
import ru.debajo.srrradio.ui.common.SnowFallPreference
import ru.debajo.srrradio.ui.common.SnowFallUseCase
import ru.debajo.srrradio.ui.host.add.AddCustomStationCommandResultReduktor
import ru.debajo.srrradio.ui.host.add.AddCustomStationReduktor
import ru.debajo.srrradio.ui.host.add.AddCustomStationViewModel
import ru.debajo.srrradio.ui.host.collection.CollectionViewModel
import ru.debajo.srrradio.ui.host.main.equalizer.EqualizerViewModel
import ru.debajo.srrradio.ui.host.main.list.StationsListViewModel
import ru.debajo.srrradio.ui.host.main.list.reduktor.StationsListCommandResultReduktor
import ru.debajo.srrradio.ui.host.main.list.reduktor.StationsListReduktor
import ru.debajo.srrradio.ui.host.main.map.MapController
import ru.debajo.srrradio.ui.host.main.map.StationsOnMapViewModel
import ru.debajo.srrradio.ui.host.main.player.PlayerBottomSheetViewModel
import ru.debajo.srrradio.ui.host.main.player.reduktor.PlayerBottomSheetCommandResultReduktor
import ru.debajo.srrradio.ui.host.main.player.reduktor.PlayerBottomSheetReduktor
import ru.debajo.srrradio.ui.host.main.playlist.DefaultPlaylistViewModel
import ru.debajo.srrradio.ui.host.main.settings.SettingsViewModel
import ru.debajo.srrradio.ui.host.main.timer.SleepTimer
import ru.debajo.srrradio.ui.host.main.timer.SleepTimerViewModel
import ru.debajo.srrradio.ui.processor.AddFavoriteStationProcessor
import ru.debajo.srrradio.ui.processor.AddTrackToCollectionProcessor
import ru.debajo.srrradio.ui.processor.AppUpdateProcessor
import ru.debajo.srrradio.ui.processor.AutoplayProcessor
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
import ru.debajo.srrradio.update.AppUpdateFlowHelper
import ru.debajo.srrradio.widget.PlayerWidgetManager

val AppModule: Module = module {
    firebase()
    sync()
    viewModel()
    preference()
    reduktor()
    processor()

    single { StationCoverLoader(get()) }
    single { SleepTimer() }
    factory { SendingErrorsManager(get(), get()) }
    single { SrrradioThemeManager(get()) }
    singleOf(::RateAppManager)
    single { GoogleServicesUtils(get()) }
    single { MediaSessionController(get()) }
    factoryOf(::PlayerVolumePreference)
    single {
        val player = RadioPlayer(get(), get(), get(), get(), get())
        MediaController(get(), player, get(), get(), get())
    }

    single { SnowFallUseCase(get(), get()) }
    factory { UserStationsInteractor(get(), get()) }
    factory { LoadM3uInteractor(get(), get(), get(), get()) }
    factory { PlaybackBroadcastReceiver(get()) }
    factory { SrrradioNotificationManager(get(), get()) }
    factoryOf(::PlayerWidgetManager)
    factoryOf(::MapController)
    factoryOf(::AppUpdateFlowHelper)
}

private fun Module.firebase() {
    single { FirebaseCrashlytics.getInstance() }
    single { FirebaseAuth.getInstance() }
    single { F.getInstance(get()) }
}

private fun Module.viewModel() {
    factoryOf(::PlayerBottomSheetViewModel)
    factoryOf(::SleepTimerViewModel)
    factoryOf(::SettingsViewModel)
    factoryOf(::AddCustomStationViewModel)
    factoryOf(::CollectionViewModel)
    factoryOf(::DefaultPlaylistViewModel)
    factoryOf(::StationsListViewModel)
    factoryOf(::StationsOnMapViewModel)
    factoryOf(::EqualizerViewModel)
}

private fun Module.preference() {
    factoryOf(::SendingErrorsPreference)
    factoryOf(::SrrradioThemePreference)
    factoryOf(::SnowFallPreference)
    factoryOf(::RateAppStatePreference)
    factoryOf(::HostActivityCreateCountPreference)
    factoryOf(::InitialAutoplayPreference)
    factoryOf(::RadioEqualizerPreference)
    factoryOf(::BluetoothAutoplayPreference)
}

private fun Module.sync() {
    singleOf(::AuthManagerProvider)
    factory { AppStateSnapshotMerger() }
    single { AppSynchronizer(get(), get(), get(), get()) }
    factoryOf(::AppStateSnapshotExtractor)
}

private fun Module.reduktor() {
    factory { StationsListReduktor(get()) }
    factoryOf(::StationsListCommandResultReduktor)
    factory { PlayerBottomSheetReduktor(get()) }
    factoryOf(::PlayerBottomSheetCommandResultReduktor)
    factory { AddCustomStationReduktor() }
    factory { AddCustomStationCommandResultReduktor() }
}

private fun Module.processor() {
    factoryOf(::SearchStationsCommandProcessor)
    factoryOf(::NewPlayCommandProcessor)
    factoryOf(::MediaStateListenerCommandProcessor)
    factoryOf(::ListenFavoriteStationsProcessor)
    factoryOf(::AddFavoriteStationProcessor)
    factoryOf(::TrackCollectionListener)
    factoryOf(::PopularStationsProcessor)
    factoryOf(::SleepTimerListenerProcessor)
    factoryOf(::AddTrackToCollectionProcessor)
    factoryOf(::SaveCustomStationProcessor)
    factoryOf(::AutoplayProcessor)
    factoryOf(::AppUpdateProcessor)
}
