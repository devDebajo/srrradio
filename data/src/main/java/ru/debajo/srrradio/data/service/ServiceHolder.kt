package ru.debajo.srrradio.data.service

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.serialization.json.Json
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
            .addConverterFactory(StreamConverterFactory(json))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(httpClient)
            .build()
            .create(RadioBrowserService::class.java)
    }

    suspend fun createService(): RadioBrowserService = service.get()
}
