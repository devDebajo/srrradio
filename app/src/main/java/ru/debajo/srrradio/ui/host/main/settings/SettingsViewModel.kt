package ru.debajo.srrradio.ui.host.main.settings

import android.content.Context
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.debajo.srrradio.error.SendErrorsHelper
import ru.debajo.srrradio.ui.host.main.settings.model.SettingsState
import ru.debajo.srrradio.ui.host.main.settings.model.SettingsTheme
import ru.debajo.srrradio.ui.processor.interactor.LoadM3uInteractor
import ru.debajo.srrradio.ui.theme.SrrradioThemeManager

internal class SettingsViewModel(
    private val themeManager: SrrradioThemeManager,
    private val loadM3uInteractor: LoadM3uInteractor,
    private val sendErrorsHelper: SendErrorsHelper,
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

    fun update() {
        viewModelScope.launch {
            stateMutable.value = stateMutable.value.copy(
                canSendLogs = sendErrorsHelper.canSend()
            )
        }
    }

    fun selectTheme(code: String) {
        themeManager.select(code)
    }

    fun onFileSelected(filePath: String) {
        if (stateMutable.value.loadingM3u) {
            return
        }
        stateMutable.value = stateMutable.value.copy(loadingM3u = true)
        viewModelScope.launch {
            loadM3uInteractor.load(filePath)
            stateMutable.value = stateMutable.value.copy(loadingM3u = false)
        }
    }

    fun sendLogs(uiContext: Context) {
        viewModelScope.launch {
//            val intent = sendErrorsHelper.buildIntent()
//            uiContext.startActivity(intent)
        }
    }

    companion object {
        val Local: ProvidableCompositionLocal<SettingsViewModel> = staticCompositionLocalOf { TODO() }
    }
}
