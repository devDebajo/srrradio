package ru.debajo.srrradio.ui.host.main.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.updatePadding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.lang.ref.WeakReference
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import ru.debajo.srrradio.di.diViewModel
import ru.debajo.srrradio.domain.LOCATION_PERMISSION
import ru.debajo.srrradio.ui.model.UiStation

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun StationsOnMapScreen(listBottomPadding: Dp) {
    val viewModel: StationsOnMapViewModel = diViewModel()
    InitMapLifecycle()
    val permissionState = rememberPermissionState(permission = LOCATION_PERMISSION) { granted ->
        if (granted) {
            viewModel.loadCurrentLocation()
        }
    }
    val stations by viewModel.stations.collectAsState()
    val mapController = remember { MapController() }
    LaunchedEffect(viewModel) {
        launch {
            viewModel.moveToLocationRequest.collect {
                mapController.moveTo(it.latitude, it.longitude, it.animated)
            }
        }

        if (permissionState.status.isGranted) {
            viewModel.loadCurrentLocation()
        } else {
            permissionState.launchPermissionRequest()
        }

        viewModel.load()
    }
    val listBottomPaddingPx = with(LocalDensity.current) { listBottomPadding.toPx() }

    AndroidView(
        factory = { context ->
            MapView(context).also { mapView ->
                mapView.setMultiTouchControls(true)
                mapController.attach(mapView.controller)
            }
        },
        update = { mapView ->
            mapView.updatePadding(bottom = listBottomPaddingPx.roundToInt())
            mapView.addStations(stations) { station -> viewModel.startPlaying(station) }
        }
    )
}

private fun MapView.addStations(stations: List<UiStation>, onStationClick: (UiStation) -> Unit) {
    val listener = object : Marker.OnMarkerClickListener {
        override fun onMarkerClick(marker: Marker, mapView: MapView): Boolean {
            val station = stations.firstOrNull { it.id == marker.id } ?: return false
            onStationClick(station)
            marker.showInfoWindow()
            mapView.invalidate()
            return true
        }
    }

    for (station in stations) {
        val location = station.location ?: continue
        val marker = Marker(this)
        marker.id = station.id
        marker.setOnMarkerClickListener(listener)
        marker.title = station.name
        marker.position = GeoPoint(location.latitude, location.longitude)
        overlays.add(marker)
    }
    invalidate()
}

private class MapController {

    private var libraryController: WeakReference<IMapController>? = null

    fun attach(controller: IMapController) {
        libraryController = WeakReference(controller)
    }

    fun moveTo(latitude: Double, longitude: Double, animated: Boolean) {
        val libraryController = libraryController?.get() ?: return
        if (animated) {
            libraryController.animateTo(GeoPoint(latitude, longitude), 15.0, 1200L)
        } else {
            libraryController.setCenter(GeoPoint(latitude, longitude))
            libraryController.setZoom(15.0)
        }
    }
}
