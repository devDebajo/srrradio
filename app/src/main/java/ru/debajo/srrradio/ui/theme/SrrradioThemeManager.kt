package ru.debajo.srrradio.ui.theme

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.debajo.srrradio.auth.setProperty

internal class SrrradioThemeManager(
    private val themePreference: SrrradioThemePreference,
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
        themePreference.set(code)
        setProperty(K, code)
    }

    private fun loadCurrent(): AppTheme {
        val key = themePreference.get()
        if (key == null) {
            setProperty(K, defaultTheme.code)
            return defaultTheme
        }

        setProperty(K, key)
        return themes.firstOrNull { it.code == key } ?: defaultTheme
    }

    companion object {
        const val K: String = "theme"
        val Local: ProvidableCompositionLocal<SrrradioThemeManager> = staticCompositionLocalOf { TODO() }

        private val defaultTheme: AppTheme = GraphiteTheme

        private val themes: List<AppTheme> = listOfNotNull(
            GraphiteTheme,
            DynamicThemeOptional,
            SynthTheme,
            BlueTheme,
            SandTheme,
            MintTheme,
        )
    }
}
