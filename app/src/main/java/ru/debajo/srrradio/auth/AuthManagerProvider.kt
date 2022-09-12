package ru.debajo.srrradio.auth

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import ru.debajo.srrradio.common.LazySuspend
import ru.debajo.srrradio.common.lazySuspend
import ru.debajo.srrradio.domain.repository.ConfigRepository
import ru.debajo.srrradio.rate.GoogleServicesUtils

class AuthManagerProvider(
    private val configRepository: ConfigRepository,
    private val firebaseAuth: FirebaseAuth,
    private val context: Context,
    private val googleServicesAvailabilityChecker: GoogleServicesUtils,
) {
    private val manager: LazySuspend<AuthManager> = lazySuspend { provideInternal() }

    suspend operator fun invoke(): AuthManager = manager.get()

    private suspend fun provideInternal(): AuthManager {
        return if (googleServicesAvailabilityChecker.servicesAvailable && configRepository.provide().authEnabled) {
            AuthManagerImpl(firebaseAuth, context)
        } else {
            NotSupportedAuthManager
        }
    }
}
