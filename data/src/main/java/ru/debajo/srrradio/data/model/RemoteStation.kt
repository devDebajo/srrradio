package ru.debajo.srrradio.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.debajo.srrradio.domain.model.LatLng
import ru.debajo.srrradio.domain.model.Station

@Serializable
internal data class RemoteStation(
    @SerialName("stationuuid")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("url_resolved")
    val stream: String,

    @SerialName("favicon")
    val image: String?,

    @SerialName("lastcheckok")
    val health: Int,

    @SerialName("tags")
    val tags: String? = null,

    @SerialName("geo_lat")
    val latitude: Double? = null,

    @SerialName("geo_long")
    val longitude: Double? = null,
) {
    val tagsList: List<String>
        get() = tags.parseTags()
}

fun String?.parseTags(): List<String> {
    if (isNullOrEmpty()) {
        return emptyList()
    }

    return split(",")
        .filter { it.isNotEmpty() }
        .map { it.trim() }
}

internal fun RemoteStation.toDomain(): Station {
    return Station(
        id = id,
        name = name.trim(),
        stream = stream,
        image = image,
        location = LatLng.from(latitude, longitude),
        alive = health == 1,
        tags = tagsList
    )
}
