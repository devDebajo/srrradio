package ru.debajo.srrradio.ui.host.main.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.withScale
import androidx.core.graphics.withTranslation
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.CustomZoomButtonsDisplay
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
                mapController.moveTo(it.location, it.animated)
            }
        }

        viewModel.load()
    }

    val accentColor = MaterialTheme.colorScheme.primaryContainer
    AndroidView(
        factory = { context ->
            MapView(context).also { mapView ->
                mapView.setMultiTouchControls(true)
                mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
                mapView.zoomController.display.setPositions(
                    false,
                    CustomZoomButtonsDisplay.HorizontalPosition.RIGHT,
                    CustomZoomButtonsDisplay.VerticalPosition.CENTER
                )
                mapView.minZoomLevel = 3.0
                mapView.maxZoomLevel = 17.0
                mapController.attach(mapView.controller)
            }
        },
        update = { mapView ->
            mapView.addStations(accentColor, stations) { station -> viewModel.startPlaying(station) }
        }
    )
}

private fun MapView.addStations(color: Color, stations: List<UiStation>, onStationClick: (UiStation) -> Unit) {
    val listener = object : Marker.OnMarkerClickListener {
        override fun onMarkerClick(marker: Marker, mapView: MapView): Boolean {
            val station = stations.firstOrNull { it.id == marker.id } ?: return false
            onStationClick(station)
            marker.showInfoWindow()
            mapView.invalidate()
            return true
        }
    }

    val icon = context.loadIcon(color)
    for (station in stations) {
        val location = station.location ?: continue
        val marker = Marker(this)
        marker.icon = icon
        marker.id = station.id
        marker.setOnMarkerClickListener(listener)
        marker.title = station.name
        marker.position = GeoPoint(location.latitude, location.longitude)
        overlays.add(marker)
    }
    invalidate()
}

private fun Context.loadIcon(color: Color): Drawable {
    val mutable = ContextCompat.getDrawable(this, ru.debajo.srrradio.R.drawable.ic_map_pin)!!.mutate()
    mutable.setTint(android.graphics.Color.BLACK)

    val bitmap = Bitmap.createBitmap(mutable.intrinsicWidth, mutable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    mutable.setBounds(0, 0, bitmap.width, bitmap.height)
    mutable.draw(canvas)

    mutable.setTint(color.toArgb())
    canvas.withScale(
        x = 0.95f,
        y = 0.95f,
        pivotX = 0.5f,
        pivotY = 0.5f,
    ) {
        mutable.draw(this)
    }

    val logo = ContextCompat.getDrawable(this, ru.debajo.srrradio.R.drawable.ic_launcher_foreground)!!
    logo.setBounds(0, 0, bitmap.width, bitmap.height)
    canvas.withTranslation(y = -10f) {
        logo.draw(canvas)
    }

    return BitmapDrawable(resources, bitmap)
}
