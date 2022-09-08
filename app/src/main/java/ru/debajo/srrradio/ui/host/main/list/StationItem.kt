@file:OptIn(ExperimentalAnimationApi::class)

package ru.debajo.srrradio.ui.host.main.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.debajo.srrradio.R
import ru.debajo.srrradio.ui.common.AppCard
import ru.debajo.srrradio.ui.ext.longPress
import ru.debajo.srrradio.ui.ext.select
import ru.debajo.srrradio.ui.ext.stringResource
import ru.debajo.srrradio.ui.host.main.player.StationCover
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.UiStationPlayingState

private val HEIGHT = 80.dp

@Composable
fun StationItem(
    modifier: Modifier = Modifier,
    station: UiStation,
    favorite: Boolean,
    playingState: UiStationPlayingState,
    onPlayClick: (UiStation, UiStationPlayingState) -> Unit,
    onFavoriteClick: (UiStation, Boolean) -> Unit,
) {
    AppCard(modifier = modifier) {
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
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 6.dp),
                text = station.name,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(8.dp))
            val haptic = LocalHapticFeedback.current
            IconButton(onClick = {
                haptic.longPress()
                onFavoriteClick(station, !favorite)
            }) {
                Icon(
                    tint = favorite.select(MaterialTheme.colorScheme.primary, LocalContentColor.current),
                    imageVector = favorite.select(Icons.Rounded.Favorite, Icons.Rounded.FavoriteBorder),
                    contentDescription = favorite.stringResource(R.string.accessibility_remove_favorite, R.string.accessibility_add_favorite)
                )
            }
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
                indication = rememberRipple(bounded = false),
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
                    contentDescription = if (state == UiStationPlayingState.PLAYING) {
                        stringResource(R.string.accessibility_pause)
                    } else {
                        stringResource(R.string.accessibility_play)
                    },
                )
            }
        }
    }
}
