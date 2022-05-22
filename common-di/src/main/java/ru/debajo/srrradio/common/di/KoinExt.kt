package ru.debajo.srrradio.common.di

import org.koin.core.module.Module

inline fun <Api : ModuleApi, Dependencies : ModuleDependencies, reified T> Module.register(
    holder: ModuleApiHolder<Api, Dependencies>,
    crossinline block: Api.() -> T
) {
    factory { holder.get().block() }
}
