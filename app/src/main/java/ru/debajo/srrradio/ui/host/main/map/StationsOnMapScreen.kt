package ru.debajo.srrradio.ui.host.main.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import ru.debajo.srrradio.di.diViewModel
import ru.debajo.srrradio.domain.LOCATION_PERMISSION
import ru.debajo.srrradio.ui.common.RequestPermission
import ru.debajo.srrradio.ui.model.UiStation

@Composable
fun StationsOnMapScreen() {
    val viewModel: StationsOnMapViewModel = diViewModel()
    val mapController = rememberMapController()
    RequestPermission(
        permission = LOCATION_PERMISSION,
        key = viewModel,
        onGrant = { viewModel.loadCurrentLocation() },
    )
    val stations by viewModel.stations.collectAsState()
    LaunchedEffect(viewModel) {
        launch {
            viewModel.moveToLocationRequest.collect {
                mapController.moveTo(it.latitude, it.longitude, it.animated)
            }
        }

        viewModel.load()
    }

    AndroidView(
        factory = { context ->
            MapView(context).also { mapView ->
                mapView.setMultiTouchControls(true)
                mapController.attach(mapView.controller)
            }
        },
        update = { mapView ->
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
