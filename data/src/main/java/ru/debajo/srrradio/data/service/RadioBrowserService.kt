package ru.debajo.srrradio.data.service

import retrofit2.http.GET
import retrofit2.http.Query
import ru.debajo.srrradio.data.model.RemoteStation

interface RadioBrowserService {
    @GET("/json/stations/search")
    suspend fun search(@Query("name") query: String): List<RemoteStation>
}
