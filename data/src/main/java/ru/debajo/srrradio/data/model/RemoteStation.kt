package ru.debajo.srrradio.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteStation(
    @SerialName("stationuuid")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("url_resolved")
    val stream: String,

    @SerialName("favicon")
    val image: String?
)
