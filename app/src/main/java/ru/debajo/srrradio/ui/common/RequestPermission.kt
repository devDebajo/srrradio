package ru.debajo.srrradio.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun RequestPermission(
    permission: String,
    key: Any,
    onDeny: () -> Unit = {},
    onGrant: () -> Unit = {},
) {
    val onDenyLatest = rememberUpdatedState(onDeny)
    val onGrantLatest = rememberUpdatedState(onGrant)

    val permissionState = rememberPermissionState(permission = permission) { granted ->
        if (granted) {
            onGrantLatest.value()
        } else {
            onDenyLatest.value()
        }
    }

    LaunchedEffect(key) {
        if (permissionState.status.isGranted) {
            onGrantLatest.value()
        } else {
            permissionState.launchPermissionRequest()
        }
    }
}
