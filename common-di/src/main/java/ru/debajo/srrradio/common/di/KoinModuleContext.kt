package ru.debajo.srrradio.common.di

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.KoinContext
import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

internal class KoinModuleContext : KoinContext {

    private var _koin: Koin? = null
    private var _koinApplication: KoinApplication? = null

    override fun get(): Koin = _koin ?: error("KoinApplication has not been started")

    override fun getOrNull(): Koin? = _koin

    fun getKoinApplicationOrNull(): KoinApplication? = _koinApplication

    override fun stopKoin() = synchronized(this) {
        _koin?.close()
        _koin = null
    }

    override fun startKoin(koinApplication: KoinApplication): KoinApplication = synchronized(this) {
        register(koinApplication)
        koinApplication.createEagerInstances()
        return koinApplication
    }

    override fun startKoin(appDeclaration: KoinAppDeclaration): KoinApplication = synchronized(this) {
        val koinApplication = KoinApplication.init()
        register(koinApplication)
        appDeclaration(koinApplication)
        koinApplication.createEagerInstances()
        return koinApplication
    }

    override fun loadKoinModules(module: Module) = synchronized(this) {
        get().loadModules(listOf(module))
    }

    override fun loadKoinModules(modules: List<Module>) = synchronized(this) {
        get().loadModules(modules)
    }

    override fun unloadKoinModules(module: Module) = synchronized(this) {
        get().unloadModules(listOf(module))
    }

    override fun unloadKoinModules(modules: List<Module>) = synchronized(this) {
        get().unloadModules(modules)
    }

    private fun register(koinApplication: KoinApplication) {
        if (_koin != null) {
            throw KoinAppAlreadyStartedException("A Koin Application has already been started")
        }
        _koinApplication = koinApplication
        _koin = koinApplication.koin
    }
}
