package ru.debajo.srrradio.common.utils

import kotlinx.coroutines.CancellationException
import timber.log.Timber

inline fun <T> runCatchingNonCancellation(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

fun <T> Result<T>.toTimber(): Result<T> = onFailure { Timber.e(it) }
