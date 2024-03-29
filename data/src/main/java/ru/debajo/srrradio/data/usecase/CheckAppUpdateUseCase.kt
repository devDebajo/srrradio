package ru.debajo.srrradio.data.usecase

import ru.debajo.srrradio.common.AppVersion
import ru.debajo.srrradio.common.GooglePlayInAppUpdateHelper
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.common.utils.toTimber
import ru.debajo.srrradio.domain.repository.ConfigRepository

class CheckAppUpdateUseCase(
    private val appVersion: AppVersion,
    private val configRepository: ConfigRepository,
    private val googlePlayInAppUpdateHelper: GooglePlayInAppUpdateHelper,
) {
    suspend fun hasUpdate(): Boolean {
        return runCatchingNonCancellation { hasUpdateUnsafe() }
            .toTimber()
            .getOrElse { false }
    }

    private suspend fun hasUpdateUnsafe(): Boolean {
        val config = configRepository.provide(force = true)
        if (googlePlayInAppUpdateHelper.installedFromGooglePlay()) {
            return if (config.googlePlayInAppUpdateEnabled) {
                googlePlayInAppUpdateHelper.updateAvailable()
            } else {
                false
            }
        }

        return config.lastVersionNumber > appVersion.number && !config.updateFileUrl.isNullOrEmpty()
    }
}
