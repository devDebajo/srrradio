package ru.debajo.srrradio.ui.host.main.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import ru.debajo.srrradio.BuildConfig
import ru.debajo.srrradio.R
import ru.debajo.srrradio.ui.ext.optionalClickable
import ru.debajo.srrradio.ui.host.LocalOpenDocumentLauncher

@Composable
fun SettingsScreen() {
    CollapsingToolbarScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        state = rememberCollapsingToolbarScaffoldState(),
        toolbar = {
            Text(
                text = stringResource(R.string.settings_title),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
            )
            Spacer(Modifier.height(8.dp))
        },
        body = { SettingsList() },
        scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed
    )
}

@Composable
private fun SettingsList() {
    val viewModel = SettingsViewModel.Local.current
    val state by viewModel.state.collectAsState()
    val expandedGroup = rememberSaveable { mutableStateOf(-1) }
    SettingsBackPress(expandedGroup)

    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(8.dp))

        SettingsGroup(
            title = stringResource(R.string.settings_app_theme),
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

        SettingsGroup(
            title = stringResource(R.string.settings_group_app),
            state = calculateGroupState(expandedGroup, 1),
            onHeaderClick = { expandedGroup.onGroupHeaderClick(1) }
        ) {
            val context = LocalContext.current
            SettingsText(
                text = stringResource(R.string.settings_privacy_policy)
            ) {
                context.openUrl(BuildConfig.PRIVACY_POLICY)
            }

            SettingsText(
                text = stringResource(R.string.settings_data_source)
            ) {
                context.openUrl(BuildConfig.DATABASE_HOMEPAGE)
            }

            SettingsText(
                text = stringResource(R.string.settings_app_version, BuildConfig.VERSION_NAME)
            )
        }

        Spacer(Modifier.height(12.dp))

        val openDocumentLauncher = LocalOpenDocumentLauncher.current
        SettingsGroup(
            title = "Данные",
            state = calculateGroupState(expandedGroup, 2),
            onHeaderClick = { expandedGroup.onGroupHeaderClick(2) }
        ) {
            SettingsText(
                text = "Импорт m3u",
                loadingIndicator = state.loadingM3u
            ) {
                if (!state.loadingM3u) {
                    openDocumentLauncher.launch(arrayOf("audio/x-mpegurl"))
                }
            }
        }
    }
}

private fun Context.openUrl(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
}

@Composable
fun SettingsText(
    text: String,
    alpha: Float = 1f,
    fontSize: TextUnit = 14.sp,
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
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .optionalClickable(onClick)
                .padding(vertical = 18.dp, horizontal = 16.dp)
                .weight(1f)
                .alpha(alphaAnimatable.value),
            text = text,
            fontSize = fontSize,
            fontWeight = fontWeight,
        )

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
