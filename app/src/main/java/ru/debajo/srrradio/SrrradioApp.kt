package ru.debajo.srrradio

import android.app.Application
import ru.debajo.srrradio.data.di.DomainDependenciesImpl
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.di.AppDependencies
import ru.debajo.srrradio.domain.di.DomainApiHolder
import timber.log.Timber

class SrrradioApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        initDi()
    }

    private fun initDi() {
        AppApiHolder.init(AppDependencies.Impl(this))
        DomainApiHolder.init(DomainDependenciesImpl)
    }
}