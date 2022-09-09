package ru.debajo.srrradio.ui.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.debajo.srrradio.ProcessScope
import ru.debajo.srrradio.domain.repository.ConfigRepository

class SnowFallUseCase(
    private val configRepository: ConfigRepository,
    private val snowFallPreference: SnowFallPreference,
) {

    private val scope by ProcessScope

    private val enabledMutable: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val enabled: StateFlow<Boolean> = enabledMutable.asStateFlow()

    init {
        scope.launch {
            enabledMutable.value = toggleAvailable() && snowFallPreference.get()
        }
    }

    suspend fun toggleAvailable(): Boolean {
        return configRepository.provide().snowFallEnabled
    }

    suspend fun updateEnabled(enabled: Boolean) {
        snowFallPreference.set(enabled)
        enabledMutable.value = toggleAvailable() && enabled
    }
}
