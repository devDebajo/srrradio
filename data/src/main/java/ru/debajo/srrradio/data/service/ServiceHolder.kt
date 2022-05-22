package ru.debajo.srrradio.data.service

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.InetAddress
import java.net.UnknownHostException

internal class ServiceHolder(
    private val json: Json,
    private val httpClient: OkHttpClient,
) {

    @Volatile
    private var service: RadioBrowserService? = null
    private val mutex = Mutex()

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun createService(): RadioBrowserService {
        if (service == null) {
            mutex.withLock {
                if (service == null) {
                    val servers = findServers()
                    val host = servers.firstOrNull() ?: "de1.api.radio-browser.info"

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

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun findServers(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val addresses = InetAddress.getAllByName("all.api.radio-browser.info")
                addresses.mapNotNull { it.canonicalHostName }
            } catch (_: UnknownHostException) {
                emptyList()
            }
        }
    }
}
