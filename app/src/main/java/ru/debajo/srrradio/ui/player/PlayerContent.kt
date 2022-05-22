package ru.debajo.srrradio.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import ru.debajo.srrradio.RadioPlayer
import ru.debajo.srrradio.ui.list.StationsListViewModel
import ru.debajo.srrradio.ui.list.reduktor.StationsListEvent
import ru.debajo.srrradio.ui.model.UiStation

@Composable
fun PlayerContent(
    playerState: RadioPlayer.State.HasStation,
    nextStation: UiStation?,
    previousStation: UiStation?,
) {
    val viewModel = StationsListViewModel.Local.current

    Column(Modifier.fillMaxSize()) {
        GlideImage(
            modifier = Modifier
                .size(270.dp)
                .align(Alignment.CenterHorizontally),
            imageModel = playerState.station.image
        )
        Text(
            text = playerState.station.name
        )
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayBackButton(
                visible = previousStation != null,
                size = 46.dp,
                icon = Icons.Rounded.SkipPrevious,
                contentDescription = "Прошлая радиостанция",
                onClick = {
                    if (previousStation != null) {
                        viewModel.onEvent(StationsListEvent.ChangeStation(previousStation))
                    }
                }
            )
            Spacer(Modifier.width(18.dp))
            PlayBackButton(
                visible = true,
                size = 80.dp,
                icon = if (playerState.playWhenReady) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = if (playerState.playWhenReady) "Пауза" else "Продолжить воспроизведение",
                onClick = { viewModel.onEvent(StationsListEvent.OnPlayPauseClick(playerState.station)) }
            )
            Spacer(Modifier.width(18.dp))
            PlayBackButton(
                visible = nextStation != null,
                size = 46.dp,
                icon = Icons.Rounded.SkipNext,
                contentDescription = "Следующая радиостанция",
                onClick = {
                    if (nextStation != null) {
                        viewModel.onEvent(StationsListEvent.ChangeStation(nextStation))
                    }
                }
            )
        }
    }
}

@Composable
private fun PlayBackButton(
    visible: Boolean,
    size: Dp,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    if (!visible) {
        Spacer(modifier = Modifier.size(size))
        return
    }
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(size / 2f))
            .background(MaterialTheme.colorScheme.primary)
            .clickable(
                indication = rememberRipple(color = MaterialTheme.colorScheme.primaryContainer),
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .semantics {
                this.contentDescription = contentDescription
            }
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            imageVector = icon,
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = null,
        )
    }
}