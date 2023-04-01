package ru.debajo.srrradio.ui.host.main.settings

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ru.debajo.srrradio.auth.AuthManagerProvider
import ru.debajo.srrradio.auth.AuthState
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.domain.repository.ConfigRepository
import ru.debajo.srrradio.error.SendingErrorsManager
import ru.debajo.srrradio.icon.AppIconManager
import ru.debajo.srrradio.rate.RateAppManager
import ru.debajo.srrradio.sync.AppSynchronizer
import ru.debajo.srrradio.ui.common.SnowFallUseCase
import ru.debajo.srrradio.ui.host.main.settings.model.SettingsAuthStatus
import ru.debajo.srrradio.ui.host.main.settings.model.SettingsNews
import ru.debajo.srrradio.ui.host.main.settings.model.SettingsState
import ru.debajo.srrradio.ui.host.main.settings.model.SettingsTheme
import ru.debajo.srrradio.ui.processor.interactor.LoadM3uInteractor
import ru.debajo.srrradio.ui.theme.SrrradioThemeManager

internal class SettingsViewModel(
    private val themeManager: SrrradioThemeManager,
    private val loadM3uInteractor: LoadM3uInteractor,
    private val sendingErrorsManager: SendingErrorsManager,
    private val appIconManager: AppIconManager,
    private val authManagerProvider: AuthManagerProvider,
    private val appSynchronizer: AppSynchronizer,
    private val snowFallUseCase: SnowFallUseCase,
    private val rateAppManager: RateAppManager,
    private val configRepository: ConfigRepository,
) : ViewModel() {

    private val stateMutable: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState())
    private val newsMutable: MutableSharedFlow<SettingsNews> = MutableSharedFlow()
    val state: StateFlow<SettingsState> = stateMutable.asStateFlow()
    val news: Flow<SettingsNews> = newsMutable.asSharedFlow()

    init {
        updateState {
            copy(
                autoSendErrors = sendingErrorsManager.isEnabled,
                dynamicIcon = appIconManager.dynamicIcon
            )
        }

        viewModelScope.launch(IO) {
            combine(
                snowFallUseCase.enabled,
                themeManager.currentTheme,
                appSynchronizer.observeLastSyncDate().onStart { emit(null) },
                authManagerProvider().authState,
            ) { snowFallEnabled, currentTheme, lastSyncDate, authState ->
                val themes = themeManager.supportedThemes.map {
                    SettingsTheme(
                        theme = it,
                        selected = it.code == currentTheme.code
                    )
                }

                val authStatus = when (authState) {
                    is AuthState.Anonymous -> SettingsAuthStatus.LOGGED_OUT
                    is AuthState.Authenticated -> SettingsAuthStatus.LOGGED_IN
                    is AuthState.Unavailable -> SettingsAuthStatus.NOT_SUPPORTED
                }

                Quadriple(snowFallEnabled, themes, lastSyncDate, authStatus)
            }.collect { (snowFallEnabled, themes, lastSyncDate, authStatus) ->
                updateState {
                    copy(
                        snowFallToggleVisible = snowFallUseCase.toggleAvailable(),
                        snowFallEnabled = snowFallEnabled,
                        themes = themes,
                        authStatus = authStatus,
                        lastSyncDate = lastSyncDate,
                    )
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

    fun snowFallClick() {
        viewModelScope.launch {
            updateState {
                snowFallUseCase.updateEnabled(!snowFallEnabled)
                copy(snowFallEnabled = !snowFallEnabled)
            }
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
            authManagerProvider().signIn()
        }
    }

    fun logout() {
        viewModelScope.launch {
            authManagerProvider().signOut()
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            appSynchronizer.deleteSyncData()
            authManagerProvider().deleteUser()
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

    fun resetRateApp() {
        rateAppManager.resetForDebug()
    }

    fun openPrivacyPolicy() {
        viewModelScope.launch {
            val privacyPolicy = configRepository.provide().privacyPolicy
            newsMutable.emit(SettingsNews.OpenUrl(privacyPolicy))
        }
    }

    fun openDatabaseHomepage() {
        viewModelScope.launch {
            val databaseHomepage = configRepository.provide().databaseHomepage
            newsMutable.emit(SettingsNews.OpenUrl(databaseHomepage))
        }
    }

    private inline fun updateState(block: SettingsState.() -> SettingsState) {
        stateMutable.value = stateMutable.value.block()
    }

    private data class Quadriple<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

    companion object {
        val Local: ProvidableCompositionLocal<SettingsViewModel> = staticCompositionLocalOf { TODO() }
    }
}
