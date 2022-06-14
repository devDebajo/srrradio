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
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.UiStationPlayingState
import ru.debajo.srrradio.ui.player.StationCover

private val HEIGHT = 80.dp

@Composable
fun StationItem(
    modifier: Modifier = Modifier,
    station: UiStation,
    playingState: UiStationPlayingState,
    onPlayClick: (UiStation, UiStationPlayingState) -> Unit,
) {
    OutlinedCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(HEIGHT)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedContent(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(40.dp),
                targetState = playingState
            ) {
                PlayPauseButton(state = it) {
                    onPlayClick(station, playingState)
                }
            }
            Spacer(Modifier.width(8.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = station.name
            )
            Spacer(Modifier.width(8.dp))
            StationCover(
                modifier = Modifier.size(HEIGHT),
                url = station.image
            )
        }
    }
}

@Composable
fun PlayPauseButton(
    modifier: Modifier = Modifier,
    state: UiStationPlayingState,
    onPlayClick: () -> Unit,
) {
    Box(
        modifier
            .size(40.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                enabled = state != UiStationPlayingState.BUFFERING,
                onClick = { onPlayClick() },
            )
    ) {
        when (state) {
            UiStationPlayingState.BUFFERING -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            UiStationPlayingState.PLAYING,
            UiStationPlayingState.NONE -> {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.primary,
                    imageVector = if (state == UiStationPlayingState.PLAYING) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = null,
                )
            }
        }
    }
}
