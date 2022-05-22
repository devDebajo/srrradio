package ru.debajo.srrradio.common.presentation

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

@Suppress("UNCHECKED_CAST")
inline fun <reified VM : ViewModel> ComponentActivity.viewModelsBy(crossinline factory: () -> VM): Lazy<VM> {
    return viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = factory() as T
        }
    }
}

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
