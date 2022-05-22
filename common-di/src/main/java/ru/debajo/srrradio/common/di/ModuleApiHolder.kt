package ru.debajo.srrradio.common.di

import org.koin.core.KoinApplication
import org.koin.core.module.Module
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Proxy

abstract class ModuleApiHolder<Api : ModuleApi, Dependencies : ModuleDependencies> {

    private var api: Api? = null

    abstract val koinModules: List<Module>

    abstract val dependencies: Dependencies

    fun get(): Api {
        return api ?: buildApi().also { api = it }
    }

    @Suppress("UNCHECKED_CAST")
    private fun buildApi(): Api {
        val context = KoinModuleContext()
        val koinApp = context.startKoin { modules(koinModules) }

        val parameterizedType = this::class.java.genericSuperclass as ParameterizedType
        val apiType = parameterizedType.actualTypeArguments[0] as Class<*>

        return Proxy.newProxyInstance(
            this::class.java.classLoader,
            arrayOf(apiType)
        ) { _, method, _ -> handleCall(koinApp, method) } as Api
    }

    private fun handleCall(koinApp: KoinApplication, method: Method): Any {
        return koinApp.koin.get(method.returnType.kotlin)
    }
}
