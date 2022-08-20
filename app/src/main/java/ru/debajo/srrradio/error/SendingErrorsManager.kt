package ru.debajo.srrradio.error

import android.content.SharedPreferences
import com.google.firebase.crashlytics.FirebaseCrashlytics

class SendingErrorsManager(
    private val sharedPreferences: SharedPreferences,
    private val firebaseCrashlytics: FirebaseCrashlytics
) {

    val isEnabled: Boolean
        get() = enabledInPrefs

    private var enabledInPrefs: Boolean
        get() = sharedPreferences.getBoolean(KEY, true)
        set(value) {
            sharedPreferences.edit()
                .putBoolean(KEY, value)
                .apply()
        }

    fun init() {
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(enabledInPrefs)
    }

    fun send(error: Throwable) {
        if (enabledInPrefs) {
            firebaseCrashlytics.recordException(error)
        }
    }

    fun updateEnabled(enabled: Boolean) {
        enabledInPrefs = enabled
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(enabled)
    }

    private companion object {
        const val KEY = "SendingErrorsManager"
    }
}
