package ru.debajo.srrradio.common.di

import android.util.Log
import org.koin.core.KoinApplication
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.Kind
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.ScopeDefinition
import org.koin.dsl.module
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.typeOf

abstract class ModuleApiHolder<Api : ModuleApi, Dependencies : ModuleDependencies> {

    private var api: Api? = null

    abstract val koinModules: List<Module>

    abstract val dependencies: Dependencies

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> inject(): Lazy<T> {
        val javaPropertyType = T::class.java
        Log.d("yopta", "javaPropertyType $javaPropertyType")
        return lazy {
            val api = get()
            Log.d("yopta", "api $api")
            val method = api.javaClass.declaredMethods.first { it.returnType == javaPropertyType }
            method.invoke(api) as T
        }
    }

    fun get(): Api {
        return api ?: buildApi().also { api = it }
    }

    @Suppress("UNCHECKED_CAST")
    open fun getApiType(): Class<out Api> {
        val parameterizedType = this::class.java.genericSuperclass as ParameterizedType
        return parameterizedType.actualTypeArguments[0] as Class<out Api>
    }

    @Suppress("UNCHECKED_CAST")
    private fun buildApi(): Api {
        val context = KoinModuleContext()
        val koinApp = context.startKoin { modules(koinModules + buildDependenciesModule()) }

        val apiType = getApiType()
        // TODO throw when several same types
        return Proxy.newProxyInstance(
            this::class.java.classLoader,
            arrayOf(apiType)
        ) { _, method, _ -> handleCall(koinApp, method) } as Api
    }

    private fun handleCall(koinApp: KoinApplication, method: Method): Any {
        return koinApp.koin.get(method.returnType.kotlin)
    }

    @Suppress("UNCHECKED_CAST")
    private fun buildDependenciesModule(): Module {
        val parameterizedType = this::class.java.genericSuperclass as ParameterizedType
        val dependenciesType = parameterizedType.actualTypeArguments[1] as Class<*>
        val methods = dependenciesType.declaredMethods.filterNotNull()

        val dependencies = dependencies
        return module {
            for (method in methods) {
                factoryReflect(clazz = method.returnType.kotlin) {
                    method.invoke(dependencies)
                }
            }
        }
    }
}

private val definitionsProperty: KProperty<*> by lazy {
    Module::class.members
        .filterIsInstance<KProperty<*>>()
        .first { it.returnType == typeOf<HashSet<BeanDefinition<*>>>() }
        .also { it.isAccessible = true }
}

@Suppress("UNCHECKED_CAST")
private fun <T : Any> Module.factoryReflect(
    qualifier: Qualifier? = null,
    override: Boolean = false,
    clazz: KClass<*>,
    definition: Definition<T>,
) {
    val beanDefinition = BeanDefinition(
        scopeQualifier = ScopeDefinition.ROOT_SCOPE_QUALIFIER,
        primaryType = clazz,
        qualifier = qualifier,
        definition = definition,
        kind = Kind.Factory,
        options = makeOptions(override),
    )

    val definitionsSet = definitionsProperty.javaGetter!!.invoke(this) as HashSet<BeanDefinition<*>>
    definitionsSet.add(beanDefinition)
}