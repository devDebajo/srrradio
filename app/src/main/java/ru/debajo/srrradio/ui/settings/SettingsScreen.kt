package ru.debajo.srrradio.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
        body = {
            SettingsList()
        },
        scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed
    )
}

@Composable
private fun SettingsList() {
    val expandedGroup = rememberSaveable { mutableStateOf(0) }
    SettingsBackPress(expandedGroup)

    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(8.dp))
        SettingsGroup(
            title = stringResource(R.string.settings_group_app),
            state = calculateGroupState(expandedGroup, 0),
            onHeaderClick = { expandedGroup.onGroupHeaderClick(0) }
        ) {
            val context = LocalContext.current
            SettingsText(
                text = stringResource(R.string.settings_privacy_policy)
            ) {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(BuildConfig.PRIVACY_POLICY))
                )
            }

            SettingsText(
                text = stringResource(R.string.settings_app_version, BuildConfig.VERSION_NAME)
            )
        }
    }
}

@Composable
fun SettingsText(
    text: String,
    alpha: Float = 1f,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight? = null,
    onClick: (() -> Unit)? = null,
) {
    val alphaAnimatable = remember(text) { Animatable(1f) }
    LaunchedEffect(key1 = text, key2 = alpha, block = {
        if (alphaAnimatable.value != alpha) {
            alphaAnimatable.animateTo(alpha)
        }
    })
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .optionalClickable(onClick)
            .padding(vertical = 18.dp, horizontal = 16.dp)
            .alpha(alphaAnimatable.value),
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
    )
}

@Composable
private fun SettingsBackPress(expandedGroup: MutableState<Int>) {
    BackHandler(enabled = expandedGroup.value != -1) {
        expandedGroup.value = -1
    }
}