package ru.debajo.srrradio.common

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun interface LazySuspend<T> {
    suspend fun get(): T
}

interface MutableLazySuspend<T> : LazySuspend<T> {
    suspend fun set(value: T)
}

private class LazySuspendImpl<T>(
    private val factory: suspend () -> T,
) : MutableLazySuspend<T> {

    @Volatile
    private var value: T? = null
    private val mutex = Mutex()

    override suspend fun get(): T {
        if (value == null) {
            mutex.withLock {
                if (value == null) {
                    value = factory()
                }
            }
        }
        return value!!
    }

    override suspend fun set(value: T) {
        mutex.withLock {
            this@LazySuspendImpl.value = value
        }
    }
}

fun <T> lazySuspend(factory: suspend () -> T): LazySuspend<T> = mutableLazySuspend(factory)

fun <T> mutableLazySuspend(factory: suspend () -> T): MutableLazySuspend<T> = LazySuspendImpl(factory)
