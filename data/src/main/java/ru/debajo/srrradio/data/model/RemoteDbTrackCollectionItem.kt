package ru.debajo.srrradio.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteDbTrackCollectionItem(
    @SerialName("name")
    val name: String? = null,

    @SerialName("stationId")
    val stationId: String? = null,

    @SerialName("stationName")
    val stationName: String? = null,
)
