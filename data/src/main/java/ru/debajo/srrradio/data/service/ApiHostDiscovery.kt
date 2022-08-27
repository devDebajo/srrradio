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
import ru.debajo.srrradio.common.lazySuspend
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation

class ApiHostDiscovery {

    private val host = lazySuspend { runCatchingNonCancellation { findServers().first() }.getOrNull() ?: DEFAULT_HOST }

    suspend fun discover() {
        supervisorScope {
            launch(Dispatchers.IO) {
                getHost()
            }
        }
    }

    suspend fun getHost(): String = host.get()

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
