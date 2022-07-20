package ru.debajo.srrradio.ui.host.main.settings

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.debajo.srrradio.ui.host.main.settings.model.SettingsState
import ru.debajo.srrradio.ui.host.main.settings.model.SettingsTheme
import ru.debajo.srrradio.ui.theme.SrrradioThemeManager

internal class SettingsViewModel(
    private val themeManager: SrrradioThemeManager
) : ViewModel() {

    private val stateMutable: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = stateMutable.asStateFlow()

    init {
        viewModelScope.launch {
            themeManager.currentTheme.collect { selected ->
                val themes = themeManager.supportedThemes.map {
                    SettingsTheme(
                        theme = it,
                        selected = it.code == selected.code
                    )
                }
                stateMutable.value = stateMutable.value.copy(
                    themes = themes
                )
            }
        }
    }

    fun selectTheme(code: String) {
        themeManager.select(code)
    }

    companion object {
        val Local: ProvidableCompositionLocal<SettingsViewModel> = staticCompositionLocalOf { TODO() }
    }
}
