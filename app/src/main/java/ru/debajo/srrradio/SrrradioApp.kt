package ru.debajo.srrradio

import android.app.Application
import android.util.Log
import fr.bipi.tressence.file.FileLoggerTree
import java.io.File
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
import ru.debajo.srrradio.media.MediaController
import timber.log.Timber

class SrrradioApp : Application(), CoroutineScope by CoroutineScope(SupervisorJob()) {

    private val mediaController: MediaController by lazy { AppApiHolder.get().mediaController }
    private val apiHostDiscovery: ApiHostDiscovery by lazy { DataApiHolder.get().apiHostDiscovery }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(logToFileTree())
        }

        initDi()
        initFatalErrors()

        launch {
            apiHostDiscovery.discover()
        }

        launch(Main) {
            mediaController.prepare()
        }
    }

    private fun initFatalErrors() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Timber.e(e)
            defaultHandler?.uncaughtException(t, e)
        }
    }

    private fun logToFileTree(): Timber.Tree {
        return FileLoggerTree.Builder()
            .withFileName("srrradio%g.log")
            .withDir(File(cacheDir, "logs"))
            .withSizeLimit(20000)
            .withFileLimit(20)
            .withMinPriority(Log.ERROR)
            .appendToFile(true)
            .build()
    }

    private fun initDi() {
        CommonApiHolder.init(CommonDependencies.Impl(this))
        DomainApiHolder.init(DomainDependenciesImpl)
        AppApiHolder.init(AppDependencies.Impl(this))
    }
}
