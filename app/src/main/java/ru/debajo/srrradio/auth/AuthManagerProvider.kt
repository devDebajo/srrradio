package ru.debajo.srrradio.auth

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import ru.debajo.srrradio.common.LazySuspend
import ru.debajo.srrradio.common.lazySuspend
import ru.debajo.srrradio.domain.repository.ConfigRepository

class AuthManagerProvider(
    private val configRepository: ConfigRepository,
    private val firebaseAuth: FirebaseAuth,
    private val context: Context,
) {
    private val manager: LazySuspend<AuthManager> = lazySuspend { provideInternal() }

    private val isPlayServicesAvailable: Boolean
        get() {
            return runCatching {
                val googleApiAvailability = GoogleApiAvailability.getInstance()
                val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
                resultCode == ConnectionResult.SUCCESS
            }.getOrElse { false }
        }

    suspend operator fun invoke(): AuthManager = manager.get()

    private suspend fun provideInternal(): AuthManager {
        return if (isPlayServicesAvailable && configRepository.provide().authEnabled) {
            AuthManagerImpl(firebaseAuth, context)
        } else {
            NotSupportedAuthManager
        }
    }
}
