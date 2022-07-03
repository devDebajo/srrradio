package ru.debajo.srrradio

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.debajo.srrradio.common.di.CommonApiHolder
import ru.debajo.srrradio.common.di.CommonDependencies
import ru.debajo.srrradio.data.di.DataApiHolder
import ru.debajo.srrradio.data.di.DomainDependenciesImpl
import ru.debajo.srrradio.data.service.ApiHostDiscovery
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.di.AppDependencies
import ru.debajo.srrradio.domain.di.DomainApiHolder
import timber.log.Timber

class SrrradioApp : Application(), CoroutineScope by CoroutineScope(SupervisorJob()) {

    private val mediaController: MediaController by lazy { AppApiHolder.get().mediaController }
    private val apiHostDiscovery: ApiHostDiscovery by lazy { DataApiHolder.get().apiHostDiscovery }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        initDi()

        launch {
            apiHostDiscovery.discover()
        }

        launch(Main) {
            mediaController.prepare()
        }
    }

    private fun initDi() {
        CommonApiHolder.init(CommonDependencies.Impl(this))
        DomainApiHolder.init(DomainDependenciesImpl)
        AppApiHolder.init(AppDependencies.Impl(this))
    }
}
