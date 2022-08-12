package ru.debajo.srrradio.ui.theme

import android.content.SharedPreferences
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class SrrradioThemeManager(
    private val sharedPreferences: SharedPreferences,
) {

    private val currentThemeMutable: MutableStateFlow<AppTheme> = MutableStateFlow(loadCurrent())
    val currentTheme: StateFlow<AppTheme> = currentThemeMutable.asStateFlow()
    val supportedThemes: List<AppTheme> = themes

    fun select(code: String) {
        val selected = themes.firstOrNull { it.code == code } ?: currentThemeMutable.value
        saveSelected(selected.code)
        currentThemeMutable.value = selected
    }

    private fun saveSelected(code: String) {
        sharedPreferences.edit()
            .putString(KEY, code)
            .apply()
    }

    private fun loadCurrent(): AppTheme {
        val key = sharedPreferences.getString(KEY, null) ?: return defaultTheme
        val currentTheme = themes.firstOrNull { it.code == key } ?: defaultTheme
        return currentTheme
    }

    companion object {
        val Local: ProvidableCompositionLocal<SrrradioThemeManager> = staticCompositionLocalOf { TODO() }

        private const val KEY = "CURRENT_THEME"

        private val defaultTheme: AppTheme = SynthTheme

        private val themes: List<AppTheme> = listOfNotNull(
            DynamicThemeOptional,
            SynthTheme,
            BlueTheme,
            MintTheme,
        )
    }
}
