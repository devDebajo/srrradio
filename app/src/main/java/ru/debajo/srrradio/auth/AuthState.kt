package ru.debajo.srrradio.auth

import com.google.firebase.auth.FirebaseUser

sealed interface AuthState {
    object Unavailable : AuthState
    object Anonymous : AuthState
    data class Authenticated(val user: FirebaseUser) : AuthState
}
