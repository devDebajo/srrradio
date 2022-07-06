package ru.debajo.srrradio.ui.ext

import androidx.compose.runtime.Composable

@Composable
fun Boolean.stringResource(
    positiveId: Int,
    negativeId: Int,
): String {
    return if (this) {
        androidx.compose.ui.res.stringResource(positiveId)
    } else {
        androidx.compose.ui.res.stringResource(negativeId)
    }
}

fun <T> Boolean.select(positive: T, negative: T): T = if (this) positive else negative
