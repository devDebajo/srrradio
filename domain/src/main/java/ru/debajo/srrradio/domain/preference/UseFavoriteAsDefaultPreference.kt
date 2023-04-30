package ru.debajo.srrradio.domain.preference

import android.content.SharedPreferences

class UseFavoriteAsDefaultPreference(sharedPreferences: SharedPreferences) : BooleanPreference(sharedPreferences) {
    override val key: String = "use_favorite_as_default"
}