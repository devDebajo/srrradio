package ru.debajo.srrradio.data.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import ru.debajo.srrradio.common.LazySuspend
import ru.debajo.srrradio.common.lazySuspend
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.common.utils.toTimber
import ru.debajo.srrradio.data.BuildConfig
import ru.debajo.srrradio.data.R
import ru.debajo.srrradio.domain.model.Config
import ru.debajo.srrradio.domain.repository.ConfigRepository

internal class ConfigRepositoryImpl(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
) : ConfigRepository {

    private val config: LazySuspend<Config> = lazySuspend { fetch() }
    private val initialized: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override suspend fun provide(): Config {
        ensureInitialized()
        return config.get()
    }

    private suspend fun ensureInitialized() {
        if (!initialized.value) {
            init()
            initialized.value = true
        }
    }

    private suspend fun init() {
        val configSettings: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
            .apply {
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) {
                    30
                } else {
                    TimeUnit.HOURS.toSeconds(1)
                }
            }
            .build()

        firebaseRemoteConfig.setConfigSettingsAsync(configSettings).await()
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults).await()
    }

    private suspend fun fetch(): Config {
        return runCatchingNonCancellation {
            firebaseRemoteConfig.fetchAndActivate().await()
            Config(
                authEnabled = firebaseRemoteConfig.getBoolean("auth_enabled"),
                snowFallEnabled = firebaseRemoteConfig.getBoolean("snow_fall_toggle_visible"),
            )
        }
            .toTimber()
            .getOrElse { Config() }
    }
}
