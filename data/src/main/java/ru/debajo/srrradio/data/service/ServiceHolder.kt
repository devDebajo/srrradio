package ru.debajo.srrradio.data.service

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import ru.debajo.srrradio.common.LazySuspend
import ru.debajo.srrradio.common.lazySuspend

internal class ServiceHolder(
    private val json: Json,
    private val httpClient: OkHttpClient,
    private val apiHostDiscovery: ApiHostDiscovery,
) {

    private var service: LazySuspend<RadioBrowserService> = lazySuspend {
        val host = apiHostDiscovery.getHost()

        Retrofit.Builder()
            .baseUrl("https://$host/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(httpClient)
            .build()
            .create(RadioBrowserService::class.java)
    }

    suspend fun createService(): RadioBrowserService = service.get()
}
