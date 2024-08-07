package ru.debajo.srrradio.ui.host.main.list

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.debajo.srrradio.ui.common.AppCard
import ru.debajo.srrradio.ui.model.UiMainTile

@Composable
fun PlaylistCard(
    modifier: Modifier = Modifier,
    item: UiMainTile,
    onClick: (UiMainTile) -> Unit,
) {
    Box(modifier = modifier) {
        RegularUiMainTile(
            item = item,
            onClick = onClick,
        )

        when (item) {
            is UiMainTile.Regular -> Unit
            is UiMainTile.Progress -> {
                if (item.loading) {
                    val progress = animateFloatAsState(item.progress, label = "CircularProgressIndicator progress")
                    CircularProgressIndicator(
                        progress = { progress.value },
                        modifier = Modifier.align(Alignment.Center),
                        strokeWidth = 2.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun RegularUiMainTile(
    modifier: Modifier = Modifier,
    item: UiMainTile,
    onClick: (UiMainTile) -> Unit,
) {
    AppCard(
        modifier = modifier.size(100.dp),
        onClick = {
            if (item.clickable) {
                onClick(item)
            }
        },
    ) {
        Box(Modifier.fillMaxSize()) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp),
                contentDescription = null,
                imageVector = item.icon,
                tint = MaterialTheme.colorScheme.primary,
            )

            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                text = stringResource(item.title),
                fontSize = 9.sp,
                lineHeight = 12.sp
            )
        }
    }
}
