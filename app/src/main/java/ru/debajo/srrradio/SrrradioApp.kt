package ru.debajo.srrradio

import android.app.Application
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ru.debajo.srrradio.common.di.CommonModule
import ru.debajo.srrradio.common.utils.inject
import ru.debajo.srrradio.data.di.DataModule
import ru.debajo.srrradio.data.service.ApiHostDiscovery
import ru.debajo.srrradio.di.AppModule
import ru.debajo.srrradio.domain.di.DomainModule
import ru.debajo.srrradio.error.OnlyErrorsTree
import ru.debajo.srrradio.error.SendingErrorsManager
import ru.debajo.srrradio.icon.AppIconManager
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.ui.theme.SrrradioThemeManager
import timber.log.Timber

class SrrradioApp : Application() {

    private val mediaController: MediaController by inject()
    private val apiHostDiscovery: ApiHostDiscovery by inject()
    private val sendingErrorsManager: SendingErrorsManager by inject()
    private val appIconManager: AppIconManager by inject()
    private val themeManager: SrrradioThemeManager by inject()

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
        startKoin {
            modules(
                module {
                    single<Context> { this@SrrradioApp }
                },
                AppModule,
                CommonModule,
                DataModule,
                DomainModule,
            )
        }
    }
}

val ProcessScopeImmediate: LifecycleCoroutineScope
    get() = ProcessLifecycleOwner.get().lifecycle.coroutineScope

val ProcessScope: Lazy<LifecycleCoroutineScope>
    get() = lazy { ProcessScopeImmediate }
