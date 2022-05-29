package ru.debajo.srrradio.common.di

import android.app.NotificationManager
import android.content.Context
import org.koin.core.module.Module
import org.koin.dsl.module

object CommonApiHolder : ExtDependenciesModuleApiHolder<CommonApi, CommonDependencies>() {
    override val koinModules: List<Module> = listOf(
        module {
            single { get<Context>().getSharedPreferences("srrradio.prefs", Context.MODE_PRIVATE) }
            single { get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
        }
    )
}
