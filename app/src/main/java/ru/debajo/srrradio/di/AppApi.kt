package ru.debajo.srrradio.di

import ru.debajo.srrradio.common.di.ModuleApi
import ru.debajo.srrradio.ui.list.StationsListViewModel

interface AppApi : ModuleApi {
    fun stationsListViewModel(): StationsListViewModel
}
