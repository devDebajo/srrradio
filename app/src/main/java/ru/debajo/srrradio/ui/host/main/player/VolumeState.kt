package ru.debajo.srrradio.ui.host.main.player

import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VolumeMute
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.debajo.srrradio.common.utils.getFromDi
import ru.debajo.srrradio.media.MediaController

@Composable
fun VolumeBar(
    modifier: Modifier = Modifier,
    volumeState: VolumeState,
) {
    Row(
        modifier = modifier
            .offset(y = volumeState.offset)
            .padding(
                end = 36.dp,
                start = 36.dp,
                top = 16.dp
            )
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary,
            imageVector = Icons.Rounded.VolumeMute,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(7.dp))
        Slider(
            modifier = Modifier.width(250.dp),
            value = volumeState.volume,
            onValueChange = { volumeState.setValue(it) },
            colors = SliderDefaults.colors(
                inactiveTrackColor = Color.Black.copy(alpha = 0.5f)
            ),
        )
        Spacer(modifier = Modifier.width(7.dp))
        Icon(
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary,
            imageVector = Icons.Rounded.VolumeUp,
            contentDescription = null,
        )
    }
}

@Stable
class VolumeState(
    private val mediaController: MediaController,
    private val coroutineScope: CoroutineScope,
) {

    var offset: Dp by mutableStateOf(HIDDEN_OFFSET)
        private set

    var volume: Float by mutableStateOf(1f)
        private set

    private var job: Job? = null

    init {
        volume = mediaController.volume
    }

    fun show() {
        coroutineScope.launch {
            animateOffset(0.dp)
            scheduleHide()
        }
    }

    fun setValue(value: Float) {
        mediaController.volume = value
        volume = value
        scheduleHide()
    }

    private fun scheduleHide() {
        job?.cancel()
        job = coroutineScope.launch {
            delay(DELAY_MS)
            animateOffset(HIDDEN_OFFSET)
        }
    }

    private suspend fun animateOffset(to: Dp) {
        animate(
            typeConverter = Dp.VectorConverter,
            initialValue = offset,
            targetValue = to,
            animationSpec = tween(300),
            block = { value, _ -> offset = value }
        )
    }

    private companion object {
        const val DELAY_MS: Long = 4000L
        val HIDDEN_OFFSET: Dp = (-100).dp
    }
}

@Composable
fun rememberVolumeState(): VolumeState {
    val mediaController = remember { getFromDi<MediaController>() }
    val coroutineScope = rememberCoroutineScope()
    return remember(mediaController, coroutineScope) {
        VolumeState(
            mediaController = mediaController,
            coroutineScope = coroutineScope
        )
    }
}

val LocalVolumeState: ProvidableCompositionLocal<VolumeState> = staticCompositionLocalOf<VolumeState> { TODO() }
