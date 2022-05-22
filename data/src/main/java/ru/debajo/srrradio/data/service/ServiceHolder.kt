package ru.debajo.srrradio.data.service

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

internal class ServiceHolder(
    private val json: Json,
    private val httpClient: OkHttpClient,
    private val apiHostDiscovery: ApiHostDiscovery,
) {

    @Volatile
    private var service: RadioBrowserService? = null
    private val mutex = Mutex()

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun createService(): RadioBrowserService {
        if (service == null) {
            mutex.withLock {
                if (service == null) {
                    val host = apiHostDiscovery.getHost()

                    service = Retrofit.Builder()
                        .baseUrl("https://$host/")
                        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                        .addCallAdapterFactory(CoroutineCallAdapterFactory())
                        .client(httpClient)
                        .build()
                        .create(RadioBrowserService::class.java)
                }
            }
        }

        return service!!
    }
}

