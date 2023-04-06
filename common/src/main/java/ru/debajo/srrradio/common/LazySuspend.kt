package ru.debajo.srrradio.common

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface LazySuspend<T> {
    suspend fun get(force: Boolean = false): T
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

    override suspend fun get(force: Boolean): T {
        return if (force) {
            mutex.withLock {
                factory().also { value = it }
            }
        } else {
            value ?: mutex.withLock {
                value ?: factory().also { value = it }
            }
        }
    }

    override suspend fun set(value: T) {
        mutex.withLock {
            this@LazySuspendImpl.value = value
        }
    }
}

fun <T> lazySuspend(factory: suspend () -> T): LazySuspend<T> = mutableLazySuspend(factory)

fun <T> mutableLazySuspend(factory: suspend () -> T): MutableLazySuspend<T> = LazySuspendImpl(factory)
