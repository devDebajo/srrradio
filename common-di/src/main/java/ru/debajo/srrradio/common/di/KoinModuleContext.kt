package ru.debajo.srrradio.common.di

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext
import org.koin.core.context.KoinContext
import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

internal class KoinModuleContext : KoinContext {

    private var _koin: Koin? = null

    override fun get(): Koin = _koin ?: error("KoinApplication has not been started")

    override fun getOrNull(): Koin? = _koin

    override fun register(koinApplication: KoinApplication) {
        if (_koin != null) {
            throw KoinAppAlreadyStartedException("A Koin Application has already been started")
        }
        _koin = koinApplication.koin
    }

    override fun stop() = synchronized(this) {
        _koin?.close()
        _koin = null
    }

    /**
     * Start a Koin Application as StandAlone
     */
    internal fun startKoin(
        koinContext: KoinContext = GlobalContext,
        koinApplication: KoinApplication
    ): KoinApplication = synchronized(this) {
        koinContext.register(koinApplication)
        koinApplication.createEagerInstances()
        return koinApplication
    }

    /**
     * Start a Koin Application as StandAlone
     */
    internal fun startKoin(
        koinContext: KoinContext = GlobalContext,
        appDeclaration: KoinAppDeclaration
    ): KoinApplication = synchronized(this) {
        val koinApplication = KoinApplication.init()
        koinContext.register(koinApplication)
        appDeclaration(koinApplication)
        koinApplication.createEagerInstances()
        return koinApplication
    }

    /**
     * load Koin module in global Koin context
     */
    internal fun loadKoinModules(module: Module) = synchronized(this) {
        get().loadModules(listOf(module))
    }

    /**
     * load Koin modules in global Koin context
     */
    internal fun loadKoinModules(modules: List<Module>) = synchronized(this) {
        get().loadModules(modules)
    }

    /**
     * unload Koin modules from global Koin context
     */
    internal fun unloadKoinModules(module: Module) = synchronized(this) {
        get().unloadModules(listOf(module))
    }

    /**
     * unload Koin modules from global Koin context
     */
    internal fun unloadKoinModules(modules: List<Module>) = synchronized(this) {
        get().unloadModules(modules)
    }
}
