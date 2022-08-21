package ru.debajo.srrradio.ui.host.main.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import ru.debajo.srrradio.domain.LOCATION_PERMISSION
import ru.debajo.srrradio.ui.common.AppCard
import ru.debajo.srrradio.ui.model.UiPlaylistIcon

@Composable
fun NearPlaylistCard(
    modifier: Modifier = Modifier,
    item: UiPlaylistIcon,
    onClick: (UiPlaylistIcon) -> Unit,
) {
    PermissionClickable(
        modifier = modifier,
        permission = LOCATION_PERMISSION,
        onClick = { onClick(item) }
    ) { innerListener ->
        PlaylistCard(
            modifier = Modifier.fillMaxSize(),
            item = item,
            onClick = { innerListener() }
        )
    }
}

@Composable
fun PlaylistCard(
    modifier: Modifier = Modifier,
    item: UiPlaylistIcon,
    onClick: (UiPlaylistIcon) -> Unit,
) {
    AppCard(
        modifier = modifier.size(100.dp),
        onClick = { onClick(item) },
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

@Composable
@Suppress("SameParameterValue")
@OptIn(ExperimentalPermissionsApi::class)
private fun PermissionClickable(
    modifier: Modifier = Modifier,
    permission: String,
    onClick: () -> Unit,
    content: @Composable BoxScope.(onClick: () -> Unit) -> Unit,
) {
    val permissionState = rememberPermissionState(permission = permission) { granted ->
        if (granted) {
            onClick()
        }
    }
    val listener = remember(onClick) {
        {
            if (permissionState.status.isGranted) {
                onClick()
            } else {
                permissionState.launchPermissionRequest()
            }
        }
    }

    Box(modifier = modifier) {
        content(listener)
    }
}
