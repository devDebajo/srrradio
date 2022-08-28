package ru.debajo.srrradio.data.model

import com.google.gson.annotations.SerializedName

internal data class RemoteDbTrackCollectionItem(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("stationId")
    val stationId: String? = null,

    @SerializedName("stationName")
    val stationName: String? = null,
)
