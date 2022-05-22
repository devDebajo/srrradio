package ru.debajo.srrradio.ui.station

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ru.debajo.srrradio.ui.model.UiStation

@Composable
fun StationItem(station: UiStation) {
    Text(station.name)
}
