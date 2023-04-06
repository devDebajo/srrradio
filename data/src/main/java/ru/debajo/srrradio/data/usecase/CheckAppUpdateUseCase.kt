package ru.debajo.srrradio.data.usecase

import android.content.Context
import android.os.Build
import ru.debajo.srrradio.common.AppVersion
import ru.debajo.srrradio.domain.repository.ConfigRepository
import timber.log.Timber

class CheckAppUpdateUseCase(
    private val context: Context,
    private val appVersion: AppVersion,
    private val configRepository: ConfigRepository,
) {
    suspend fun hasUpdate(): Boolean {
        try {
            if (installedFromGooglePlay()) {
                return false
            }
        } catch (e: Throwable) {
            Timber.e(e)
            return false
        }

        val config = configRepository.provide(force = true)
        return config.lastVersionNumber > appVersion.number && !config.updateFileUrl.isNullOrEmpty()
    }

    @Suppress("DEPRECATION")
    private fun installedFromGooglePlay(): Boolean {
        val packageName = context.packageName

        val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.packageManager.getInstallSourceInfo(packageName).installingPackageName
        } else {
            context.packageManager.getInstallerPackageName(packageName)
        }

        return installer == "com.android.vending" || installer == "com.google.android.feedback"
    }
}
