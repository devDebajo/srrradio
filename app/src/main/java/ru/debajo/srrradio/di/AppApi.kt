package ru.debajo.srrradio.di

import ru.debajo.srrradio.MediaController
import ru.debajo.srrradio.common.di.ModuleApi
import ru.debajo.srrradio.ui.list.StationsListViewModel
import ru.debajo.srrradio.ui.player.PlayerBottomSheetViewModel

interface AppApi : ModuleApi {
    fun stationsListViewModel(): StationsListViewModel
    fun playerBottomSheetViewModel(): PlayerBottomSheetViewModel
    fun mediaController(): MediaController
}
