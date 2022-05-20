package ru.debajo.srrradio.common.presentation

import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

@MainThread
fun <T> Flow<T>.collect(scope: CoroutineScope, @MainThread collector: (T) -> Unit) {
    scope.launch(Dispatchers.Main) { this@collect.collect(collector) }
}

@MainThread
fun Lifecycle.asCoroutineScope(): Lazy<CoroutineScope> {
    return lazy(mode = LazyThreadSafetyMode.NONE) {
        val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                coroutineScope.cancel()
            }
        })

        coroutineScope
    }
}
