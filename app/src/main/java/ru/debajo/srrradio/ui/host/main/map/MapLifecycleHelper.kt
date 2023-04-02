package ru.debajo.srrradio.ui.host.main.map

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.osmdroid.config.Configuration
import org.osmdroid.config.IConfigurationProvider
import ru.debajo.srrradio.common.utils.getFromDi

class MapLifecycleHelper(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) {
    private val configuration: IConfigurationProvider by lazy { Configuration.getInstance() }

    fun load() {
        configuration.load(context, sharedPreferences)
    }

    fun save() {
        configuration.save(context, sharedPreferences)
    }
}

@Composable
fun InitMapLifecycle() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapHelper = remember { getFromDi<MapLifecycleHelper>() }
    LaunchedEffect(mapHelper) {
        mapHelper.load()
    }
    DisposableEffect(mapHelper, lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapHelper.load()
                Lifecycle.Event.ON_RESUME -> mapHelper.load()
                Lifecycle.Event.ON_PAUSE -> mapHelper.save()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            mapHelper.save()
            lifecycle.removeObserver(observer)
        }
    }
}
