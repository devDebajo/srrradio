package ru.debajo.srrradio.ui.host.main.settings

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.debajo.srrradio.auth.AuthManager
import ru.debajo.srrradio.auth.AuthState
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.domain.repository.ConfigRepository
import ru.debajo.srrradio.error.SendingErrorsManager
import ru.debajo.srrradio.icon.AppIconManager
import ru.debajo.srrradio.sync.AppSynchronizer
import ru.debajo.srrradio.ui.host.main.settings.model.SettingsAuthStatus
import ru.debajo.srrradio.ui.host.main.settings.model.SettingsState
import ru.debajo.srrradio.ui.host.main.settings.model.SettingsTheme
import ru.debajo.srrradio.ui.processor.interactor.LoadM3uInteractor
import ru.debajo.srrradio.ui.theme.SrrradioThemeManager

internal class SettingsViewModel(
    private val themeManager: SrrradioThemeManager,
    private val loadM3uInteractor: LoadM3uInteractor,
    private val sendingErrorsManager: SendingErrorsManager,
    private val appIconManager: AppIconManager,
    private val authManager: AuthManager,
    private val appSynchronizer: AppSynchronizer,
    private val configRepository: ConfigRepository,
) : ViewModel() {

    private val stateMutable: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = stateMutable.asStateFlow()

    init {
        updateState {
            copy(
                autoSendErrors = sendingErrorsManager.isEnabled,
                dynamicIcon = appIconManager.dynamicIcon
            )
        }

        viewModelScope.launch {
            themeManager.currentTheme.collect { selected ->
                val themes = themeManager.supportedThemes.map {
                    SettingsTheme(
                        theme = it,
                        selected = it.code == selected.code
                    )
                }
                updateState {
                    copy(themes = themes)
                }
            }
        }

        viewModelScope.launch {
            val config = configRepository.provide()
            if (config.authEnabled) {
                authManager.authState.collect { authState ->
                    updateState {
                        copy(
                            authStatus = when (authState) {
                                is AuthState.Anonymous -> SettingsAuthStatus.LOGGED_OUT
                                is AuthState.Authenticated -> SettingsAuthStatus.LOGGED_IN
                            }
                        )
                    }
                }
            } else {
                updateState {
                    copy(authStatus = SettingsAuthStatus.NOT_SUPPORTED)
                }
            }
        }

        viewModelScope.launch(IO) {
            appSynchronizer.observeLastSyncDate().collect {
                updateState {
                    copy(lastSyncDate = it)
                }
            }
        }
    }

    fun onAutoSendErrorsClick() {
        updateState {
            sendingErrorsManager.updateEnabled(!autoSendErrors)
            copy(autoSendErrors = !autoSendErrors)
        }
    }

    fun selectTheme(code: String) {
        themeManager.select(code)
    }

    fun onFileSelected(filePath: String) {
        if (stateMutable.value.loadingM3u) {
            return
        }
        updateState { copy(loadingM3u = true) }
        viewModelScope.launch {
            loadM3uInteractor.load(filePath)
            updateState { copy(loadingM3u = false) }
        }
    }

    fun onDynamicIconClick() {
        updateState {
            appIconManager.dynamicIcon = !dynamicIcon
            copy(dynamicIcon = !dynamicIcon)
        }
    }

    fun login() {
        viewModelScope.launch {
            authManager.signIn()
        }
    }

    fun logout() {
        authManager.signOut()
    }

    fun deleteUser() {
        viewModelScope.launch {
            appSynchronizer.deleteSyncData()
            authManager.deleteUser()
        }
    }

    fun sync() {
        if (stateMutable.value.synchronization) {
            return
        }
        viewModelScope.launch(IO) {
            updateState {
                copy(synchronization = true)
            }
            runCatchingNonCancellation { appSynchronizer.sync() }
            updateState {
                copy(
                    autoSendErrors = sendingErrorsManager.isEnabled,
                    dynamicIcon = appIconManager.dynamicIcon
                )
            }
            updateState {
                copy(synchronization = false)
            }
        }
    }

    private inline fun updateState(block: SettingsState.() -> SettingsState) {
        stateMutable.value = stateMutable.value.block()
    }

    companion object {
        val Local: ProvidableCompositionLocal<SettingsViewModel> = staticCompositionLocalOf { TODO() }
    }
}
