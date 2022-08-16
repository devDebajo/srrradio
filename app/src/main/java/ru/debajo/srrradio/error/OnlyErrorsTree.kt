package ru.debajo.srrradio.error

import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.debajo.srrradio.di.AppApiHolder
import timber.log.Timber

class OnlyErrorsTree : Timber.Tree() {

    private val firebaseCrashlytics: FirebaseCrashlytics by lazy {
        AppApiHolder.get().firebaseCrashlytics
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t != null) {
            firebaseCrashlytics.recordException(t)
        }
    }
}