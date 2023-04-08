package ru.debajo.srrradio.data.service

import java.net.InetAddress
import java.net.UnknownHostException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import ru.debajo.srrradio.common.LazySuspend
import ru.debajo.srrradio.common.lazySuspend
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.domain.repository.ConfigRepository

class ApiHostDiscovery(
    private val configRepository: ConfigRepository,
) {

    private val host: LazySuspend<String> = lazySuspend {
        runCatchingNonCancellation { findServers().first() }.getOrNull() ?: configRepository.provide().defaultApiHost
    }

    suspend fun discover() {
        getHost()
    }

    suspend fun getHost(): String = host.get()

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun findServers(): Flow<String> {
        return flow {
            val addresses = try {
                val discoverHost = configRepository.provide().discoverApiHost
                InetAddress.getAllByName(discoverHost)
            } catch (_: UnknownHostException) {
                emptyArray()
            }
            emitAll(addresses.asFlow().mapNotNull { it.canonicalHostName })
        }
    }
}
