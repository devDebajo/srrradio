package ru.debajo.srrradio.data.config

import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import ru.debajo.srrradio.common.AppVersion
import ru.debajo.srrradio.common.LazySuspend
import ru.debajo.srrradio.common.lazySuspend
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.common.utils.toTimber
import ru.debajo.srrradio.data.BuildConfig
import ru.debajo.srrradio.data.R
import ru.debajo.srrradio.domain.model.Config
import ru.debajo.srrradio.domain.repository.ConfigRepository
import timber.log.Timber

internal class ConfigRepositoryImpl(
    private val context: Context,
    private val appVersion: AppVersion,
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
) : ConfigRepository {

    private val config: LazySuspend<Config> = lazySuspend { fetch() }
    private val initialized: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override suspend fun provide(force: Boolean): Config {
        ensureInitialized()
        return config.get(force)
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
                databaseHomepage = firebaseRemoteConfig.getString("database_home_page"),
                privacyPolicy = firebaseRemoteConfig.getString("privacy_policy"),
                defaultApiHost = firebaseRemoteConfig.getString("default_api_host"),
                discoverApiHost = firebaseRemoteConfig.getString("discover_api_host"),
                authEnabled = firebaseRemoteConfig.getBoolean("auth_enabled"),
                snowFallEnabled = firebaseRemoteConfig.getBoolean("snow_fall_toggle_visible"),
                rateAppEnabled = firebaseRemoteConfig.getBoolean("rate_app_enabled"),
                lastVersionNumber = firebaseRemoteConfig.getLong("last_version_number").toInt(),
                updateFileUrl = firebaseRemoteConfig.getString("update_url").takeIf { it.isNotEmpty() },
                googlePlayInAppUpdateEnabled = firebaseRemoteConfig.getBoolean("enable_google_play_in_app_update"),
            )
        }
            .onSuccess {
                Timber.tag("ConfigRepositoryImpl").d("Remote config fetch success: $it")
            }
            .toTimber()
            .getOrElse { defaultConfig() }
    }

    private fun defaultConfig(): Config {
        return Config(
            databaseHomepage = context.getString(R.string.default_config_database_home_page),
            privacyPolicy = context.getString(R.string.default_config_privacy_policy),
            defaultApiHost = context.getString(R.string.default_config_default_api_host),
            discoverApiHost = context.getString(R.string.default_config_discover_api_host),
            authEnabled = false,
            snowFallEnabled = false,
            rateAppEnabled = false,
            lastVersionNumber = appVersion.number,
            updateFileUrl = null,
            googlePlayInAppUpdateEnabled = false,
        )
    }
}
