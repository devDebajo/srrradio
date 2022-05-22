@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package ru.debajo.srrradio.ui.station

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
            AnimatedContent(
                modifier = Modifier.padding(start = 4.dp).size(40.dp),
                targetState = station.playingState
            ) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            enabled = station.playingState != UiStationPlayingState.BUFFERING,
                            onClick = { onPlayClick(station) },
                        )
                ) {
                    when (it) {
                        UiStationPlayingState.BUFFERING -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp).align(Alignment.Center),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        UiStationPlayingState.PLAYING,
                        UiStationPlayingState.NONE -> {
                            Icon(
                                modifier = Modifier.size(24.dp).align(Alignment.Center),
                                tint = MaterialTheme.colorScheme.primary,
                                imageVector = if (station.playingState == UiStationPlayingState.PLAYING) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = null,
                            )
                        }
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
