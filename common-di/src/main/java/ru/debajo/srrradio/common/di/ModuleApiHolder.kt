package ru.debajo.srrradio.common.di

abstract class ModuleApiHolder<Api : ModuleApi> {

    private var api: Api? = null

//    @Suppress("UNCHECKED_CAST")
//    inline fun <reified T> inject(): Lazy<T> {
//        val javaPropertyType = T::class.java
//        return lazy {
//            val api = get()
//            val method = api.javaClass.declaredMethods.first { it.returnType == javaPropertyType }
//            method.invoke(api) as T
//        }
//    }

    fun get(): Api {
        return api ?: buildApi().also { api = it }
    }

    abstract fun buildApi(): Api
}
