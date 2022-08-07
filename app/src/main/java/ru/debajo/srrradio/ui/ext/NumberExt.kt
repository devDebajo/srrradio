package ru.debajo.srrradio.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Composable
fun Int.toDp(): Dp = toDp(LocalDensity.current)

fun Int.toDp(density: Density): Dp = with(density) { toDp() }

@Composable
fun Float.toDp(): Dp = with(LocalDensity.current) { toDp() }
