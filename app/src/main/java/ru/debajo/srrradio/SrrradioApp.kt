package ru.debajo.srrradio

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import ru.debajo.srrradio.common.di.CommonApiHolder
import ru.debajo.srrradio.common.di.CommonDependencies
import ru.debajo.srrradio.data.di.DataApiHolder
import ru.debajo.srrradio.data.di.DomainDependenciesImpl
import ru.debajo.srrradio.data.service.ApiHostDiscovery
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.domain.di.DomainApiHolder
import ru.debajo.srrradio.error.OnlyErrorsTree
import ru.debajo.srrradio.error.SendingErrorsManager
import ru.debajo.srrradio.icon.AppIconManager
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.ui.theme.SrrradioThemeManager
import timber.log.Timber

class SrrradioApp : Application() {

    private val mediaController: MediaController by lazy { AppApiHolder.get().mediaController }
    private val apiHostDiscovery: ApiHostDiscovery by lazy { DataApiHolder.get().apiHostDiscovery }
    private val sendingErrorsManager: SendingErrorsManager by lazy { AppApiHolder.get().sendingErrorsManager }
    private val appIconManager: AppIconManager by lazy { AppApiHolder.get().appIconManager }
    private val themeManager: SrrradioThemeManager by lazy { AppApiHolder.get().themeManager }

    override fun onCreate() {
        super.onCreate()

        initDi()
        initLogs()
        initFatalErrors()

        val processLifecycle = ProcessLifecycleOwner.get().lifecycle
        val processScope = processLifecycle.coroutineScope

        processLifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                appIconManager.enable(themeManager.currentTheme.value.icon)
            }
        })

        with(processScope) {
            launch {
                apiHostDiscovery.discover()
            }

            launch(Main) {
                mediaController.prepare()
            }
        }
    }

    private fun initLogs() {
        sendingErrorsManager.init()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(OnlyErrorsTree(sendingErrorsManager))
        }
    }

    private fun initFatalErrors() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Timber.e(e)
            defaultHandler?.uncaughtException(t, e)
        }
    }

    private fun initDi() {
        CommonApiHolder.init(CommonDependencies.Impl(this))
        DomainApiHolder.init(DomainDependenciesImpl)
    }
}

val ProcessScopeImmediate: LifecycleCoroutineScope
    get() = ProcessLifecycleOwner.get().lifecycle.coroutineScope

val ProcessScope: Lazy<LifecycleCoroutineScope>
    get() = lazy { ProcessScopeImmediate }
