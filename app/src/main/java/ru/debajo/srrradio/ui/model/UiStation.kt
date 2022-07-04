package ru.debajo.srrradio.ui.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import ru.debajo.srrradio.domain.model.Station

@Immutable
data class UiStation(
    val id: String,
    val name: String,
    val stream: String,
    val image: String?,
)

internal fun Station.toUi(): UiStation {
    return UiStation(
        id = id,
        name = name,
        stream = stream,
        image = image
    )
}

internal fun UiStation.toDomain(): Station {
    return Station(
        id = id,
        name = name,
        stream = stream,
        image = image
    )
}

@Stable
interface UiElement {
    val id: String
    val contentType: String
}

@Immutable
data class UiStationElement(
    val station: UiStation,
    val playingState: UiStationPlayingState,
) : UiElement {
    override val id: String = station.id
    override val contentType: String = "UiStationElement"
}