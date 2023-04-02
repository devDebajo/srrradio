package ru.debajo.srrradio.ui.model

import androidx.compose.runtime.Immutable
import ru.debajo.srrradio.domain.model.LatLng
import ru.debajo.srrradio.domain.model.Station

@Immutable
data class UiStation(
    val id: String,
    val name: String,
    val stream: String,
    val image: String?,
    val location: LatLng?,
    val alive: Boolean,
    val tags: List<String>,
)

internal fun Station.toUi(): UiStation {
    return UiStation(
        id = id,
        name = name,
        stream = stream,
        image = image,
        location = location,
        alive = alive,
        tags = tags,
    )
}

internal fun UiStation.toDomain(): Station {
    return Station(
        id = id,
        name = name,
        stream = stream,
        image = image,
        location = location,
        alive = alive,
        tags = tags,
    )
}
