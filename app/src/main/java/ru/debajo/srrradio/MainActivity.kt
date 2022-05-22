package ru.debajo.srrradio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import ru.debajo.srrradio.domain.di.DomainApiHolder
import ru.debajo.srrradio.ui.theme.SrrradioTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val url = "https://radiorecord.hostingradio.ru/synth96.aacp"
//
//        val exoPlayer = ExoPlayer
//            .Builder(this)
//            .build()
//
//        val dataSourceFactory = DefaultDataSource.Factory(applicationContext)
//        exoPlayer.addMediaSource(ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(url)))

        //exoPlayer.addMediaItem(MediaItem.fromUri(url))
        //exoPlayer.prepare()
        //exoPlayer.playWhenReady = true

        lifecycleScope.launch(IO) {
            val stations = DomainApiHolder.get().searchStationsUseCase().search("synthwave")
            Timber.d(stations.toString())
        }

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
