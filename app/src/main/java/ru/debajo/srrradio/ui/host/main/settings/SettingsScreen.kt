package ru.debajo.srrradio.ui.host.main.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import org.joda.time.DateTime
import ru.debajo.srrradio.BuildConfig
import ru.debajo.srrradio.R
import ru.debajo.srrradio.ui.common.AppScreenTitle
import ru.debajo.srrradio.ui.common.alert.LocalAlertDialogState
import ru.debajo.srrradio.ui.ext.optionalClickable
import ru.debajo.srrradio.ui.host.LocalOpenDocumentLauncher
import ru.debajo.srrradio.ui.host.main.settings.model.SettingsAuthStatus
import ru.debajo.srrradio.ui.host.main.settings.model.SettingsNews
import ru.debajo.srrradio.ui.navigation.NavTree

@Composable
fun SettingsScreen(bottomPadding: Dp) {
    CollapsingToolbarScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        state = rememberCollapsingToolbarScaffoldState(),
        toolbar = {
            AppScreenTitle(text = stringResource(R.string.settings_title))
            Spacer(Modifier.height(8.dp))
        },
        body = { SettingsList(bottomPadding) },
        scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed
    )
}

@Composable
private fun SettingsList(bottomPadding: Dp) {
    val viewModel = SettingsViewModel.Local.current
    val state by viewModel.state.collectAsState()
    val expandedGroup = rememberSaveable { mutableStateOf(-1) }
    SettingsBackPress(expandedGroup)

    val context = LocalContext.current
    LaunchedEffect(viewModel, context) {
        viewModel.news.collect { news ->
            when (news) {
                is SettingsNews.OpenUrl -> context.openUrl(news.url)
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = bottomPadding)
    ) {
        Spacer(Modifier.height(8.dp))

        SettingsGroup(
            title = stringResource(R.string.settings_group_app_theme),
            state = calculateGroupState(expandedGroup, 0),
            onHeaderClick = { expandedGroup.onGroupHeaderClick(0) }
        ) {
            for (theme in state.themes) {
                SettingsColor(
                    text = stringResource(theme.theme.nameRes),
                    color = theme.theme.colors().primary,
                    selected = theme.selected,
                ) {
                    viewModel.selectTheme(theme.theme.code)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        val openDocumentLauncher = LocalOpenDocumentLauncher.current
        SettingsGroup(
            title = stringResource(R.string.settings_group_data),
            state = calculateGroupState(expandedGroup, 1),
            onHeaderClick = { expandedGroup.onGroupHeaderClick(1) }
        ) {
            SettingsText(
                text = stringResource(R.string.settings_import_m3u),
                loadingIndicator = state.loadingM3u
            ) {
                if (!state.loadingM3u) {
                    openDocumentLauncher.launch(arrayOf("audio/x-mpegurl"))
                }
            }

            SettingsSwitch(
                text = stringResource(R.string.settings_auto_send_errors),
                checked = state.autoSendErrors,
                onClick = { viewModel.onAutoSendErrorsClick() }
            )

            if (state.authStatus == SettingsAuthStatus.LOGGED_IN) {
                SettingsText(
                    text = stringResource(R.string.settings_sync),
                    loadingIndicator = state.synchronization,
                    subtitle = state.lastSyncDate?.let {
                        stringResource(R.string.settings_last_sync, it.format())
                    },
                ) {
                    viewModel.sync()
                }
            }
        }

        if (state.authStatus != SettingsAuthStatus.NOT_SUPPORTED) {
            Spacer(Modifier.height(12.dp))
            SettingsGroup(
                title = stringResource(R.string.settings_group_account),
                state = calculateGroupState(expandedGroup, 2),
                onHeaderClick = { expandedGroup.onGroupHeaderClick(2) }
            ) {
                when (state.authStatus) {
                    SettingsAuthStatus.LOGGED_IN -> {
                        SettingsText(text = stringResource(R.string.settings_logout)) {
                            viewModel.logout()
                        }
                        val alertDialogState = LocalAlertDialogState.current
                        SettingsText(text = stringResource(R.string.settings_delete_account)) {
                            alertDialogState.alert(
                                title = R.string.settings_delete_account,
                                content = R.string.settings_delete_account_confirmation,
                                confirm = R.string.settings_delete_account_confirmation_sure,
                                dismiss = R.string.settings_delete_account_confirmation_cancel,
                                onConfirm = { viewModel.deleteUser() }
                            )
                        }
                    }
                    SettingsAuthStatus.LOGGED_OUT -> {
                        SettingsText(text = stringResource(R.string.settings_login)) {
                            viewModel.login()
                        }
                    }
                    SettingsAuthStatus.NOT_SUPPORTED -> Unit
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        SettingsGroup(
            title = stringResource(R.string.settings_group_playback),
            state = calculateGroupState(expandedGroup, 3),
            onHeaderClick = { expandedGroup.onGroupHeaderClick(3) }
        ) {
            SettingsSwitch(
                text = stringResource(R.string.settings_initial_autoplay),
                checked = state.initialAutoplay,
                onClick = { viewModel.onInitialAutoplayClick() }
            )

            BluetoothAutoplaySettingsSwitch(
                text = stringResource(R.string.settings_bluetooth_autoplay),
                checked = state.bluetoothAutoplay,
                onClick = { checked ->
                    viewModel.onBluetoothAutoplayChanged(checked)
                }
            )

            val navTree = NavTree.current
            SettingsText(
                text = stringResource(R.string.settings_equalizer)
            ) {
                navTree.equalizer.navigate()
            }
        }

        Spacer(Modifier.height(12.dp))

        SettingsGroup(
            title = stringResource(R.string.settings_group_app),
            state = calculateGroupState(expandedGroup, 4),
            onHeaderClick = { expandedGroup.onGroupHeaderClick(4) }
        ) {
            if (state.snowFallToggleVisible) {
                SettingsSwitch(
                    text = stringResource(R.string.settings_snow_fall),
                    checked = state.snowFallEnabled,
                    onClick = { viewModel.snowFallClick() }
                )
            }

            SettingsSwitch(
                text = stringResource(R.string.settings_favorite_as_default),
                checked = state.useFavoriteAsDefault,
                onClick = { viewModel.useFavoriteAsDefaultClick() }
            )

            SettingsText(
                text = stringResource(R.string.settings_privacy_policy)
            ) {
                viewModel.openPrivacyPolicy()
            }

            SettingsText(
                text = stringResource(R.string.settings_data_source)
            ) {
                viewModel.openDatabaseHomepage()
            }

            SettingsText(
                text = stringResource(R.string.settings_app_version, BuildConfig.VERSION_NAME)
            )
        }

        if (BuildConfig.DEBUG) {
            Spacer(Modifier.height(12.dp))
            DebugGroup(
                expandedGroup = expandedGroup,
                groupIndex = 5,
                viewModel = viewModel,
            )
        }
    }
}

@Composable
@Suppress("SameParameterValue")
private fun DebugGroup(
    expandedGroup: MutableState<Int>,
    groupIndex: Int,
    viewModel: SettingsViewModel,
) {
    SettingsGroup(
        title = stringResource(R.string.settings_group_debug),
        state = calculateGroupState(expandedGroup, groupIndex),
        onHeaderClick = { expandedGroup.onGroupHeaderClick(groupIndex) }
    ) {
        SettingsText(
            text = stringResource(R.string.settings_clear_rate_app_data)
        ) {
            viewModel.resetRateApp()
        }
    }
}

private fun DateTime.format(): String {
    return toString("dd:MM:yy, HH:mm")
}

private fun Context.openUrl(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
}

@Composable
fun SettingsText(
    text: String,
    subtitle: String? = null,
    alpha: Float = 1f,
    fontSize: TextUnit = 14.sp,
    subtitleFontSize: TextUnit = 10.sp,
    fontWeight: FontWeight? = null,
    loadingIndicator: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val alphaAnimatable = remember(text) { Animatable(1f) }
    LaunchedEffect(key1 = text, key2 = alpha, block = {
        if (alphaAnimatable.value != alpha) {
            alphaAnimatable.animateTo(alpha)
        }
    })
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .optionalClickable(onClick)
                .padding(vertical = 18.dp, horizontal = 16.dp)
                .weight(1f)
                .alpha(alphaAnimatable.value),
        ) {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = fontWeight,
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = subtitleFontSize,
                )
            }
        }

        if (loadingIndicator) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
fun SettingsSwitch(text: String, checked: Boolean, onClick: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        )

        Spacer(Modifier.width(8.dp))
        val haptic = LocalHapticFeedback.current
        Switch(
            checked = checked,
            onCheckedChange = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick(it)
            }
        )
    }
}

@Composable
fun SettingsColor(
    text: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 18.dp),
            text = text,
        )
        Box(
            Modifier
                .border(
                    width = 1.dp,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(7.dp)
                )
                .padding(3.dp)
        ) {
            Box(
                Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
private fun SettingsBackPress(expandedGroup: MutableState<Int>) {
    BackHandler(enabled = expandedGroup.value != -1) {
        expandedGroup.value = -1
    }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun BluetoothAutoplaySettingsSwitch(
    text: String,
    checked: Boolean,
    onClick: (Boolean) -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val permissionState = rememberPermissionState(permission = Manifest.permission.BLUETOOTH_CONNECT) {
            if (it) {
                onClick(true)
            }
        }
        SettingsSwitch(
            text = text,
            checked = checked,
            onClick = { newChecked ->
                if (newChecked) {
                    if (permissionState.status is PermissionStatus.Granted) {
                        onClick(true)
                    } else {
                        permissionState.launchPermissionRequest()
                    }
                } else {
                    onClick(false)
                }
            }
        )
    } else {
        SettingsSwitch(
            text = text,
            checked = checked,
            onClick = onClick
        )
    }
}
