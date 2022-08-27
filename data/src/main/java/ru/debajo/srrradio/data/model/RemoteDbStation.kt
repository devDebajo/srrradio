package ru.debajo.srrradio.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteDbStation(
    @SerialName("id")
    val id: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("stream")
    val stream: String? = null,

    @SerialName("image")
    val image: String? = null,

    @SerialName("geo_lat")
    val latitude: Double? = null,

    @SerialName("geo_long")
    val longitude: Double? = null,
)
