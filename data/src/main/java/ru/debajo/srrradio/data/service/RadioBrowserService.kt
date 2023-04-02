package ru.debajo.srrradio.data.service

import retrofit2.http.GET
import retrofit2.http.Query
import ru.debajo.srrradio.data.model.RemoteStation

internal interface RadioBrowserService {
    @GET("/json/stations/search")
    suspend fun search(
        @Query("name") query: String? = null,
        @Query("has_geo_info") hasGeoInfo: Boolean? = null,
        @Query("hidebroken") hideBroken: Boolean? = null,
    ): List<RemoteStation>

    @GET("/json/stations/byurl")
    suspend fun byUrl(@Query("url") url: String): List<RemoteStation>

    @GET("/json/stations/lastchange")
    suspend fun newStations(
        @Query("limit") limit: Int,
        @Query("hidebroken") hideBroken: Boolean,
    ): List<RemoteStation>

    @GET("/json/stations/topvote")
    suspend fun popularStations(
        @Query("limit") limit: Int,
        @Query("hidebroken") hideBroken: Boolean,
    ): List<RemoteStation>
}
