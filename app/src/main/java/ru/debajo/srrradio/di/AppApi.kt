package ru.debajo.srrradio.di

import kotlinx.coroutines.CoroutineScope
import ru.debajo.srrradio.MediaController
import ru.debajo.srrradio.common.di.ModuleApi
import ru.debajo.srrradio.ui.list.StationsListViewModel
import ru.debajo.srrradio.ui.player.PlayerBottomSheetViewModel
import ru.debajo.srrradio.ui.timer.SleepTimer
import ru.debajo.srrradio.ui.timer.SleepTimerViewModel

interface AppApi : ModuleApi {
    val coroutineScope: CoroutineScope
    val stationsListViewModel: StationsListViewModel
    val playerBottomSheetViewModel: PlayerBottomSheetViewModel
    val sleepTimerViewModel: SleepTimerViewModel
    val mediaController: MediaController
    val sleepTimer: SleepTimer
}
