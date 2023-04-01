package ru.debajo.srrradio.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.background
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.unit.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import ru.debajo.srrradio.R
import ru.debajo.srrradio.service.PlaybackBroadcastReceiver
import ru.debajo.srrradio.ui.host.HostActivity
import ru.debajo.srrradio.ui.model.UiStationPlayingState

class PlayerWidget : GlanceAppWidget() {

    @Composable
    override fun Content() {
        val prefs: Preferences = currentState()
        val state = PlayerWidgetManager.extractState(prefs)
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(
                    day = Color.DarkGray.copy(alpha = 0.5f),
                    night = Color.DarkGray.copy(alpha = 0.5f)
                )
                .cornerRadius(16.dp)
                .padding(8.dp)
        ) {
            PlayerWidgetContent(
                modifier = GlanceModifier.fillMaxSize(),
                state = state
            )
        }
    }

    @Composable
    private fun PlayerWidgetContent(
        modifier: GlanceModifier = GlanceModifier,
        state: PlayerWidgetManager.WidgetState,
    ) {
        val context = LocalContext.current
        Column(
            modifier = modifier.clickable(actionStartActivity(HostActivity.createIntent(context))),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlanceText(
                text = state.stationName,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 1,
            )
            GlanceText(
                text = state.playingTitle.orEmpty(),
                color = Color.White.copy(alpha = 0.2f),
                fontSize = 12.sp,
                maxLines = 1,
            )
            val buttonSize = 50.dp
            Spacer(GlanceModifier.height(8.dp))
            Row {
                if (state.hasPreviousStation) {
                    ImagePlaybackButton(
                        modifier = GlanceModifier.size(buttonSize),
                        onClick = actionSendBroadcast(PlaybackBroadcastReceiver.previousIntent(context)),
                        imageRes = R.drawable.ic_skip_previous
                    )
                } else {
                    Spacer(GlanceModifier.width(buttonSize))
                }
                Spacer(GlanceModifier.width(8.dp))

                when (state.playingState) {
                    UiStationPlayingState.BUFFERING -> {
                        PlaybackButton(modifier = GlanceModifier.size(buttonSize)) {
                            CircularProgressIndicator(
                                modifier = GlanceModifier.size(buttonSize).padding(4.dp),
                                color = ColorProvider(Color.White, Color.White)
                            )
                        }
                    }
                    UiStationPlayingState.PLAYING -> {
                        ImagePlaybackButton(
                            modifier = GlanceModifier.size(buttonSize),
                            onClick = actionSendBroadcast(PlaybackBroadcastReceiver.pauseIntent(context)),
                            imageRes = R.drawable.ic_pause
                        )
                    }
                    UiStationPlayingState.NONE -> {
                        ImagePlaybackButton(
                            modifier = GlanceModifier.size(buttonSize),
                            onClick = actionSendBroadcast(PlaybackBroadcastReceiver.resumeIntent(context)),
                            imageRes = R.drawable.ic_play
                        )
                    }
                }

                Spacer(GlanceModifier.width(8.dp))
                if (state.hasNextStation) {
                    ImagePlaybackButton(
                        modifier = GlanceModifier.size(buttonSize),
                        onClick = actionSendBroadcast(PlaybackBroadcastReceiver.nextIntent(context)),
                        imageRes = R.drawable.ic_skip_next
                    )
                } else {
                    Spacer(GlanceModifier.width(buttonSize))
                }
            }
        }
    }

    @Composable
    private fun GlanceText(
        modifier: GlanceModifier = GlanceModifier,
        text: String,
        color: Color? = null,
        fontSize: TextUnit? = null,
        maxLines: Int = Int.MAX_VALUE,
    ) {
        Text(
            modifier = modifier,
            text = text,
            style = TextStyle(
                color = color?.let { ColorProvider(color, color) },
                fontSize = fontSize
            ),
            maxLines = maxLines
        )
    }

    @Composable
    private fun ImagePlaybackButton(
        modifier: GlanceModifier = GlanceModifier,
        onClick: Action,
        imageRes: Int,
    ) {
        PlaybackButton(modifier = modifier) {
            Image(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clickable(onClick),
                provider = ImageProvider(imageRes),
                contentDescription = null
            )
        }
    }

    @Composable
    private fun PlaybackButton(
        modifier: GlanceModifier = GlanceModifier,
        content: @Composable () -> Unit,
    ) {
        Box(
            modifier = modifier
                .background(
                    Color.White.copy(alpha = 0.2f),
                    Color.White.copy(alpha = 0.2f),
                )
                .cornerRadius(100.dp),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

class PlayerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: PlayerWidget = PlayerWidget()
}
