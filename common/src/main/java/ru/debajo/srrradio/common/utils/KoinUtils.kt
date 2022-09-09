package ru.debajo.srrradio.common.utils

import org.koin.java.KoinJavaComponent

inline fun <reified T : Any> inject(): Lazy<T> {
    return KoinJavaComponent.inject(T::class.java)
}

inline fun <reified T : Any> getFromDi(): T {
    return KoinJavaComponent.get(T::class.java)
}