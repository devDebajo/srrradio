package ru.debajo.srrradio.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.ref.WeakReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.asDeferred
import ru.debajo.srrradio.R

class AuthManager(
    private val firebaseAuth: FirebaseAuth,
    private val context: Context,
) {

    private var activityRef: WeakReference<Activity>? = null
    private val activity: Activity?
        get() = activityRef?.get()

    private val authStateMutable: MutableStateFlow<AuthState> = MutableStateFlow(getCurrentAuthState(firebaseAuth.currentUser))
    val authState: StateFlow<AuthState> = authStateMutable.asStateFlow()

    val currentUser: FirebaseUser?
        get() = (authState.value as? AuthState.Authenticated)?.user

    private val oneTapClient = Identity.getSignInClient(context)

    init {
        firebaseAuth.addAuthStateListener {
            authStateMutable.value = getCurrentAuthState(it.currentUser)
        }
    }

    fun setActivity(activity: ComponentActivity) {
        activityRef = WeakReference(activity)
    }

    suspend fun signIn() {
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


        val beginSignInResult = runCatching {
            oneTapClient.beginSignIn(signInRequest).asDeferred().await()
        }.getOrNull()

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

    fun signOut() {
        firebaseAuth.signOut()
    }

    suspend fun deleteUser() {
        runCatching {
            currentUser?.delete()?.asDeferred()?.await()
            signOut()
        }
    }

    suspend fun onActivityResult(requestCode: Int, data: Intent?) {
        if (requestCode != SIGN_IN_REQUEST) {
            return
        }
        val idToken = runCatching {
            val signInCredential = oneTapClient.getSignInCredentialFromIntent(data)
            signInCredential.googleIdToken
        }.getOrNull()

        if (idToken == null) {
            authStateMutable.value = getCurrentAuthState(firebaseAuth.currentUser)
        } else {
            authStateMutable.value = firebaseAuthWithGoogle(idToken)
        }
    }

    private suspend fun firebaseAuthWithGoogle(idToken: String): AuthState {
        val authResult = runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).asDeferred().await()
        }.getOrNull()

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
