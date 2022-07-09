package ru.debajo.srrradio.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun <T> rememberMutableState(initial: T): MutableState<T> {
    return remember { mutableStateOf(initial) }
}