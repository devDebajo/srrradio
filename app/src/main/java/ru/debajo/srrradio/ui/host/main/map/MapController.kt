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
import java.lang.ref.WeakReference
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.config.IConfigurationProvider
import org.osmdroid.util.GeoPoint
import ru.debajo.srrradio.common.utils.getFromDi
import ru.debajo.srrradio.domain.model.LatLng

class MapController(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) {

    private val configuration: IConfigurationProvider by lazy { Configuration.getInstance() }
    private var libraryController: WeakReference<IMapController>? = null

    fun attach(controller: IMapController) {
        libraryController = WeakReference(controller)
    }

    fun moveTo(location: LatLng, animated: Boolean) {
        val libraryController = libraryController?.get() ?: return
        if (animated) {
            libraryController.animateTo(GeoPoint(location.latitude, location.longitude), 15.0, 1200L)
        } else {
            libraryController.setCenter(GeoPoint(location.latitude, location.longitude))
            libraryController.setZoom(15.0)
        }
    }

    fun load() {
        configuration.load(context, sharedPreferences)
    }

    fun save() {
        configuration.save(context, sharedPreferences)
    }
}

@Composable
fun rememberMapController(): MapController {
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapController = remember { getFromDi<MapController>() }
    LaunchedEffect(mapController) {
        mapController.load()
    }
    DisposableEffect(mapController, lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapController.load()
                Lifecycle.Event.ON_RESUME -> mapController.load()
                Lifecycle.Event.ON_PAUSE -> mapController.save()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            mapController.save()
            lifecycle.removeObserver(observer)
        }
    }

    return mapController
}
