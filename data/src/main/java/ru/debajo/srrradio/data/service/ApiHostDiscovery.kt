package ru.debajo.srrradio.data.service

import java.net.InetAddress
import java.net.UnknownHostException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ApiHostDiscovery {

    private val mutex = Mutex()

    @Volatile
    private var host: String? = null

    suspend fun discover() {
        supervisorScope {
            launch(Dispatchers.IO) {
                getHost()
            }
        }
    }

    suspend fun getHost(): String {
        if (host == null) {
            mutex.withLock {
                if (host == null) {
                    host = runCatching { findServers().first() }.getOrNull() ?: DEFAULT_HOST
                }
            }
        }
        return host!!
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun findServers(): Flow<String> {
        return flow {
            val addresses = try {
                InetAddress.getAllByName("all.api.radio-browser.info")
            } catch (_: UnknownHostException) {
                emptyArray()
            }
            emitAll(addresses.asFlow().mapNotNull { it.canonicalHostName })
        }
    }

    private companion object {
        const val DEFAULT_HOST = "de1.api.radio-browser.info"
    }
}
