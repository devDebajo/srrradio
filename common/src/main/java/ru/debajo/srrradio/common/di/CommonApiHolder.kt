package ru.debajo.srrradio.common.di

import android.content.Context
import org.koin.core.module.Module
import org.koin.dsl.module

object CommonApiHolder : ExtDependenciesModuleApiHolder<CommonApi, CommonDependencies>() {
    override val koinModules: List<Module> = listOf(
        module {
            single { dependencies.context }
            single { dependencies.context.getSharedPreferences("srrradio.prefs", Context.MODE_PRIVATE) }
        }
    )
}
