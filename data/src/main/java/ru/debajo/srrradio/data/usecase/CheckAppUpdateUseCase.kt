package ru.debajo.srrradio.data.usecase

import ru.debajo.srrradio.common.AppVersion
import ru.debajo.srrradio.domain.repository.ConfigRepository

class CheckAppUpdateUseCase(
    private val appVersion: AppVersion,
    private val configRepository: ConfigRepository,
) {
    suspend fun hasUpdate(): Boolean {
        val config = configRepository.provide(force = true)
        return config.lastVersionNumber > appVersion.number && !config.updateFileUrl.isNullOrEmpty()
    }
}
