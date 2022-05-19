package ru.debajo.srrradio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import ru.debajo.srrradio.ui.theme.SrrradioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = "https://radiorecord.hostingradio.ru/synth96.aacp"

        val exoPlayer = ExoPlayer
            .Builder(this)
            .build()

        val dataSourceFactory = DefaultDataSource.Factory(applicationContext)
        exoPlayer.addMediaSource(ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(url)))

        //exoPlayer.addMediaItem(MediaItem.fromUri(url))
        //exoPlayer.prepare()
        //exoPlayer.playWhenReady = true

        setContent {
            SrrradioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                }
            }
        }
    }
}
