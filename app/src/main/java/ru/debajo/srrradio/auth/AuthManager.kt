package ru.debajo.srrradio.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.debajo.srrradio.ProcessScopeImmediate
import ru.debajo.srrradio.R
import ru.debajo.srrradio.common.ActivityHolder
import ru.debajo.srrradio.common.utils.getFromDi
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.common.utils.toTimber

typealias F = FirebaseAnalytics

fun log(name: String, vararg args: Pair<String, String>) {
    getFromDi<F>().log(name, *args)
}

fun F.log(name: String, vararg args: Pair<String, String>) {
    val nameToLog = if (args.isEmpty()) {
        name
    } else {
        name + args.joinToString(prefix = "_") { "${it.first}_${it.second}" }
    }
    logEvent(nameToLog, null)
}

interface AuthManager {
    val authState: StateFlow<AuthState>

    val currentUser: FirebaseUser?
        get() = (authState.value as? AuthState.Authenticated)?.user

    suspend fun signIn()

    fun signOut()

    suspend fun deleteUser()

    suspend fun onActivityResult(requestCode: Int, data: Intent?)
}

object NotSupportedAuthManager : AuthManager {
    override val authState: StateFlow<AuthState> = MutableStateFlow(AuthState.Unavailable)
    override suspend fun signIn() = Unit
    override fun signOut() = Unit
    override suspend fun deleteUser() = Unit
    override suspend fun onActivityResult(requestCode: Int, data: Intent?) = Unit
}

class AuthManagerImpl(
    private val firebaseAuth: FirebaseAuth,
    private val context: Context,
    private val activityHolder: ActivityHolder,
) : AuthManager {

    private val activity: Activity?
        get() = activityHolder.currentActivity

    private val authStateMutable: MutableStateFlow<AuthState> = MutableStateFlow(getCurrentAuthState(firebaseAuth.currentUser))
    override val authState: StateFlow<AuthState> = authStateMutable.asStateFlow()

    private val oneTapClient: SignInClient = Identity.getSignInClient(context)

    init {
        firebaseAuth.addAuthStateListener {
            authStateMutable.value = getCurrentAuthState(it.currentUser)
        }

        ProcessScopeImmediate.launch {
            runCatchingNonCancellation {
                firebaseAuth.currentUser?.reload()?.await()
            }
        }
    }

    override suspend fun signIn() {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()

        val beginSignInResult = runCatchingNonCancellation {
            oneTapClient.beginSignIn(signInRequest).await()
        }.toTimber().getOrNull()

        if (beginSignInResult == null) {
            authStateMutable.value = getCurrentAuthState(firebaseAuth.currentUser)
            return
        }

        activity?.startIntentSenderForResult(
            beginSignInResult.pendingIntent.intentSender,
            SIGN_IN_REQUEST,
            null,
            0,
            0,
            0
        )
    }

    override fun signOut() {
        firebaseAuth.signOut()
        authStateMutable.value = getCurrentAuthState(null)
    }

    override suspend fun deleteUser() {
        runCatchingNonCancellation { currentUser?.delete()?.await() }.toTimber()
        signOut()
    }

    override suspend fun onActivityResult(requestCode: Int, data: Intent?) {
        if (requestCode != SIGN_IN_REQUEST) {
            return
        }
        val idToken = runCatchingNonCancellation {
            val signInCredential = oneTapClient.getSignInCredentialFromIntent(data)
            signInCredential.googleIdToken
        }
            .toTimber()
            .getOrNull()

        if (idToken == null) {
            authStateMutable.value = getCurrentAuthState(firebaseAuth.currentUser)
        } else {
            authStateMutable.value = firebaseAuthWithGoogle(idToken)
        }
    }

    private suspend fun firebaseAuthWithGoogle(idToken: String): AuthState {
        val authResult = runCatchingNonCancellation {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
        }
            .toTimber()
            .getOrNull()

        return getCurrentAuthState(authResult?.user)
    }

    private fun getCurrentAuthState(user: FirebaseUser?): AuthState {
        return user
            ?.let { AuthState.Authenticated(it) }
            ?: AuthState.Anonymous
    }

    private companion object {
        const val SIGN_IN_REQUEST = 123456789
    }
}
