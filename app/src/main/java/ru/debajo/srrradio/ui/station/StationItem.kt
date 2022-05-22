@file:OptIn(ExperimentalMaterial3Api::class)

package ru.debajo.srrradio.ui.station

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.UiStationPlayingState

private val HEIGHT = 80.dp

@Composable
fun StationItem(
    modifier: Modifier = Modifier,
    station: UiStation,
    onPlayClick: (UiStation) -> Unit,
) {
    OutlinedCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(HEIGHT)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (station.playingState) {
                UiStationPlayingState.BUFFERING -> CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                )
                UiStationPlayingState.PLAYING,
                UiStationPlayingState.NONE -> {
                    IconButton(onClick = { onPlayClick(station) }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.primary,
                            imageVector = if (station.playingState == UiStationPlayingState.PLAYING) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = null,
                        )
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = station.name
            )
            Spacer(Modifier.width(8.dp))
            GlideImage(
                modifier = Modifier.size(HEIGHT),
                imageModel = station.image,
            )
        }
    }
}
