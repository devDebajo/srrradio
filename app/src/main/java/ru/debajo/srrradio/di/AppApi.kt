package ru.debajo.srrradio.di

import kotlinx.coroutines.CoroutineScope
import ru.debajo.srrradio.common.di.ModuleApi
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.ui.host.add.AddCustomStationViewModel
import ru.debajo.srrradio.ui.host.main.list.StationsListViewModel
import ru.debajo.srrradio.ui.host.main.player.PlayerBottomSheetViewModel
import ru.debajo.srrradio.ui.host.main.settings.SettingsViewModel
import ru.debajo.srrradio.ui.host.main.timer.SleepTimer
import ru.debajo.srrradio.ui.host.main.timer.SleepTimerViewModel
import ru.debajo.srrradio.ui.theme.SrrradioThemeManager

internal interface AppApi : ModuleApi {
    val coroutineScope: CoroutineScope

    val stationsListViewModel: StationsListViewModel
    val playerBottomSheetViewModel: PlayerBottomSheetViewModel
    val sleepTimerViewModel: SleepTimerViewModel
    val addCustomStationViewModel: AddCustomStationViewModel
    val settingsViewModel: SettingsViewModel

    val themeManager: SrrradioThemeManager

    val mediaController: MediaController
    val sleepTimer: SleepTimer
}
