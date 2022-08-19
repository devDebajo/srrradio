package ru.debajo.srrradio.common.di

abstract class ModuleApiHolder<Api : ModuleApi> {

    private var api: Api? = null

    fun get(): Api {
        return api ?: buildApi().also { api = it }
    }

    abstract fun buildApi(): Api
}
