package ru.debajo.srrradio.common

import android.content.Context
import android.os.Build
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.tasks.await

class GooglePlayInAppUpdateHelper(
    private val context: Context,
    private val appUpdateManager: AppUpdateManager,
    private val intentForResultStarterHolder: IntentForResultStarterHolder,
) {
    @Suppress("DEPRECATION")
    fun installedFromGooglePlay(): Boolean {
        val packageName = context.packageName

        val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.packageManager.getInstallSourceInfo(packageName).installingPackageName
        } else {
            context.packageManager.getInstallerPackageName(packageName)
        }

        return installer == "com.android.vending" || installer == "com.google.android.feedback"
    }

    suspend fun updateAvailable(): Boolean {
        val appUpdateInfo = appUpdateManager.appUpdateInfo.await()
        return appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
    }

    suspend fun update() {
        val currentStarter = intentForResultStarterHolder.currentStarter?.wrap() ?: return
        val appUpdateInfo = appUpdateManager.appUpdateInfo.await()
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.IMMEDIATE,
            currentStarter,
            REQUEST
        )
    }

    private fun IntentForResultStarter.wrap(): IntentSenderForResultStarter {
        val receiver = this
        return IntentSenderForResultStarter { intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options ->
            receiver.startIntentForResult(
                intent = intent,
                requestCode = requestCode,
                fillInIntent = fillInIntent,
                flagsMask = flagsMask,
                flagsValues = flagsValues,
                extraFlags = extraFlags,
                options = options
            )
        }
    }

    private companion object {
        const val REQUEST = 123
    }
}
