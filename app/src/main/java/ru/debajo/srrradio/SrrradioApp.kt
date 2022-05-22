package ru.debajo.srrradio

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.debajo.srrradio.common.di.CommonApiHolder
import ru.debajo.srrradio.common.di.CommonDependencies
import ru.debajo.srrradio.data.di.DataApiHolder
import ru.debajo.srrradio.data.di.DomainDependenciesImpl
import ru.debajo.srrradio.domain.di.DomainApiHolder
import timber.log.Timber

class SrrradioApp : Application(), CoroutineScope by CoroutineScope(SupervisorJob()) {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        initDi()

        launch {
            DataApiHolder.get().apiHostDiscovery().discover()
        }
    }

    private fun initDi() {
        CommonApiHolder.init(CommonDependencies.Impl(this))
        DomainApiHolder.init(DomainDependenciesImpl)
    }
}