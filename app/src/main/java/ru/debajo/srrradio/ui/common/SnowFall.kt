package ru.debajo.srrradio.ui.common

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import ru.debajo.srrradio.R

@Composable
@SuppressLint("InflateParams")
fun SnowFall(
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = {
            val inflater = LayoutInflater.from(it)
            inflater.inflate(R.layout.snawfall_view, null, false)
        }
    )
}