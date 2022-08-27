package ru.debajo.srrradio.error

import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.debajo.srrradio.BuildConfig

class SendingErrorsManager(
    sendingErrorsPreference: SendingErrorsPreference,
    private val firebaseCrashlytics: FirebaseCrashlytics
) {

    val isEnabled: Boolean
        get() = enabledInPrefs

    private var enabledInPrefs: Boolean by sendingErrorsPreference

    fun init() {
        updateEnabledInternal(enabledInPrefs)
    }

    fun send(error: Throwable) {
        if (enabledInPrefs) {
            firebaseCrashlytics.recordException(error)
        }
    }

    fun updateEnabled(enabled: Boolean) {
        enabledInPrefs = enabled
        updateEnabledInternal(enabled)
    }

    private fun updateEnabledInternal(enabled: Boolean) {
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG && enabled)
    }
}
