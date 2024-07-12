package ru.debajo.srrradio.auth

import com.google.firebase.auth.FirebaseUser

sealed interface AuthState {
    data object Unavailable : AuthState
    data object Anonymous : AuthState
    data class Authenticated(val user: FirebaseUser) : AuthState
}
