package ru.debajo.srrradio.data.service

import retrofit2.http.GET
import retrofit2.http.Query
import ru.debajo.srrradio.data.model.RemoteStation

internal interface RadioBrowserService {
    @GET("/json/stations/search")
    suspend fun search(@Query("name") query: String): List<RemoteStation>

    @GET("/json/stations/byurl")
    suspend fun byUrl(@Query("url") url: String): List<RemoteStation>
}
