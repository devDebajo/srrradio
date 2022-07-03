package ru.debajo.srrradio.di

import kotlinx.coroutines.CoroutineScope
import ru.debajo.srrradio.MediaController
import ru.debajo.srrradio.common.di.ModuleApi
import ru.debajo.srrradio.ui.list.StationsListViewModel
import ru.debajo.srrradio.ui.player.PlayerBottomSheetViewModel

interface AppApi : ModuleApi {
    val coroutineScope: CoroutineScope
    val stationsListViewModel: StationsListViewModel
    val playerBottomSheetViewModel: PlayerBottomSheetViewModel
    val mediaController: MediaController
}
