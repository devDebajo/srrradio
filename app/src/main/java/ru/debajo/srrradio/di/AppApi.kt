package ru.debajo.srrradio.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import ru.debajo.srrradio.common.di.ModuleApi
import ru.debajo.srrradio.error.SendErrorsHelper
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.media.MediaSessionController
import ru.debajo.srrradio.ui.host.add.AddCustomStationViewModel
import ru.debajo.srrradio.ui.host.collection.CollectionViewModel
import ru.debajo.srrradio.ui.host.main.list.StationsListViewModel
import ru.debajo.srrradio.ui.host.main.player.PlayerBottomSheetViewModel
import ru.debajo.srrradio.ui.host.main.playlist.DefaultPlaylistViewModel
import ru.debajo.srrradio.ui.host.main.settings.SettingsViewModel
import ru.debajo.srrradio.ui.host.main.settings.logs.LogsListViewModel
import ru.debajo.srrradio.ui.host.main.timer.SleepTimer
import ru.debajo.srrradio.ui.host.main.timer.SleepTimerViewModel
import ru.debajo.srrradio.ui.theme.SrrradioThemeManager

internal interface AppApi : ModuleApi {
    val coroutineScope: CoroutineScope
    val firebaseCrashlytics: FirebaseCrashlytics

    val stationsListViewModel: StationsListViewModel
    val playerBottomSheetViewModel: PlayerBottomSheetViewModel
    val sleepTimerViewModel: SleepTimerViewModel
    val addCustomStationViewModel: AddCustomStationViewModel
    val settingsViewModel: SettingsViewModel
    val collectionViewModel: CollectionViewModel
    val logsListViewModel: LogsListViewModel
    val defaultPlaylistViewModel: DefaultPlaylistViewModel

    val themeManager: SrrradioThemeManager

    val mediaController: MediaController
    val mediaSessionController: MediaSessionController
    val sleepTimer: SleepTimer
    val sendErrorsHelper: SendErrorsHelper
}
