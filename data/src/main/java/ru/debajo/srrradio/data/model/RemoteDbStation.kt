package ru.debajo.srrradio.data.model

import com.google.gson.annotations.SerializedName

internal data class RemoteDbStation(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("stream")
    val stream: String? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("lat")
    val latitude: Double? = null,

    @SerializedName("lon")
    val longitude: Double? = null,
)
