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
            IconButton(onClick = { onPlayClick(station) }) {
                Icon(
                    imageVector = if (station.playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = null,
                )
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
