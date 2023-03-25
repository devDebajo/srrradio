package ru.debajo.srrradio.rate

import android.content.ActivityNotFoundException
import android.content.Context
import ru.debajo.srrradio.BuildConfig
import ru.debajo.srrradio.R
import ru.debajo.srrradio.domain.repository.ConfigRepository
import timber.log.Timber

class RateAppManager(
    private val configRepository: ConfigRepository,
    private val googleServicesUtils: GoogleServicesUtils,
    private val rateAppStatePreference: RateAppStatePreference,
    private val hostActivityCreateCountPreference: HostActivityCreateCountPreference,
) {

    suspend fun getRateActions(): List<RateAction> {
        if (!googleServicesUtils.googlePlayAppInstalled || !configRepository.provide().rateAppEnabled) {
            return emptyList()
        }
        val count = hostActivityCreateCountPreference.get()
        Timber.tag("RateAppManager").d("App open count: $count")
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

    enum class RateAction(val res: Int) {
        OPEN_GOOGLE_PLAY(R.string.rate_app_rate_button),
        LATER(R.string.rate_app_later_button),
        NEVER(R.string.rate_app_never_button)
    }

    private companion object {
        const val TARGET_COUNT: Int = 5
    }
}
