package ru.debajo.srrradio.common.di

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.KoinContext
import org.koin.core.error.KoinAppAlreadyStartedException
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

    fun startKoin(appDeclaration: KoinAppDeclaration): KoinApplication = synchronized(this) {
        val koinApplication = KoinApplication.init()
        register(koinApplication)
        appDeclaration(koinApplication)
        koinApplication.createEagerInstances()
        return koinApplication
    }
}
