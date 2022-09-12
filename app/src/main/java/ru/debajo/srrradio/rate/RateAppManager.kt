package ru.debajo.srrradio.rate

import android.content.ActivityNotFoundException
import android.content.Context
import ru.debajo.srrradio.BuildConfig
import timber.log.Timber

class RateAppManager(
    private val googleServicesUtils: GoogleServicesUtils,
    private val rateAppStatePreference: RateAppStatePreference,
    private val hostActivityCreateCountPreference: HostActivityCreateCountPreference,
) {

    fun getRateActions(): List<RateAction> {
        if (!googleServicesUtils.googlePlayAppInstalled) {
            return emptyList()
        }
        val count = hostActivityCreateCountPreference.get()
        if (count < TARGET_COUNT) {
            return emptyList()
        }

        return when (rateAppStatePreference.get()) {
            RateAppState.RATED,
            RateAppState.NEVER -> emptyList()
            RateAppState.LATER -> {
                if (count % TARGET_COUNT == 0) {
                    listOf(RateAction.NEVER, RateAction.OPEN_GOOGLE_PLAY)
                } else {
                    emptyList()
                }
            }
            RateAppState.NOT_RATED -> {
                if (count % TARGET_COUNT == 0) {
                    listOf(RateAction.LATER, RateAction.OPEN_GOOGLE_PLAY)
                } else {
                    emptyList()
                }
            }
        }
    }

    fun hostActivityCreated() {
        hostActivityCreateCountPreference.increment()
    }

    fun onRateAction(activityContext: Context, action: RateAction) {
        when (action) {
            RateAction.OPEN_GOOGLE_PLAY -> {
                try {
                    activityContext.startActivity(googleServicesUtils.googlePlayIntent)
                    rateAppStatePreference.set(RateAppState.RATED)
                } catch (e: ActivityNotFoundException) {
                    Timber.e(e)
                }
            }
            RateAction.LATER -> rateAppStatePreference.set(RateAppState.LATER)
            RateAction.NEVER -> rateAppStatePreference.set(RateAppState.NEVER)
        }
    }

    fun resetForDebug() {
        if (!BuildConfig.DEBUG) {
            return
        }
        rateAppStatePreference.set(RateAppState.NOT_RATED)
        hostActivityCreateCountPreference.set(0)
    }

    enum class RateAction {
        OPEN_GOOGLE_PLAY,
        LATER,
        NEVER
    }

    private companion object {
        const val TARGET_COUNT: Int = 5
    }
}
