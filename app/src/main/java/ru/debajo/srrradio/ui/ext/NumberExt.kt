package ru.debajo.srrradio.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Int.toDp(): Dp = with(LocalDensity.current) { toDp() }

@Composable
fun Float.toDp(): Dp = with(LocalDensity.current) { toDp() }
