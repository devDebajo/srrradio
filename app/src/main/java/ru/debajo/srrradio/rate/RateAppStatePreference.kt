package ru.debajo.srrradio.rate

import android.content.SharedPreferences
import ru.debajo.srrradio.domain.preference.BasePreference
import ru.debajo.srrradio.domain.preference.getIntOrThrow

class RateAppStatePreference(sharedPreferences: SharedPreferences) : BasePreference<RateAppState>(sharedPreferences) {
    override val key: String = "rate_state"

    override fun defaultValue(): RateAppState = RateAppState.NOT_RATED

    override fun SharedPreferences.Editor.write(key: String, value: RateAppState): SharedPreferences.Editor {
        return putInt(key, value.code)
    }

    override fun SharedPreferences.read(key: String): RateAppState {
        return RateAppState.fromCode(getIntOrThrow(key))
    }
}
