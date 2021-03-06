package ru.debajo.srrradio.ui.host.main.player

import android.text.TextUtils
import android.widget.TextView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeProgress
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.skydoves.landscapist.glide.GlideImage
import kotlin.math.absoluteValue
import kotlin.math.sin
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ru.debajo.srrradio.R
import ru.debajo.srrradio.ui.ext.colorInt
import ru.debajo.srrradio.ui.ext.select
import ru.debajo.srrradio.ui.ext.stringResource
import ru.debajo.srrradio.ui.ext.toDp
import ru.debajo.srrradio.ui.host.main.list.PlayPauseButton
import ru.debajo.srrradio.ui.host.main.player.model.PlayerBottomSheetEvent
import ru.debajo.srrradio.ui.host.main.player.model.PlayerBottomSheetState
import ru.debajo.srrradio.ui.host.main.timer.SleepTimerViewModel
import ru.debajo.srrradio.ui.model.UiStationPlayingState

val PlayerBottomSheetPeekHeight = 60.dp

@ExperimentalMaterialApi
val SwipeProgress<BottomSheetValue>.normalizedFraction: Float
    get() {
        return when {
            from == BottomSheetValue.Collapsed && to == BottomSheetValue.Collapsed -> 0f
            from == BottomSheetValue.Expanded && to == BottomSheetValue.Expanded -> 1f
            from == BottomSheetValue.Expanded && to == BottomSheetValue.Collapsed -> 1f - fraction
            else -> fraction
        }
    }

@Composable
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
fun PlayerBottomSheetContent(scaffoldState: BottomSheetScaffoldState) {
    val viewModel = PlayerBottomSheetViewModel.Local.current
    val sleepTimerViewModel = SleepTimerViewModel.Local.current
    val state: PlayerBottomSheetState by viewModel.state.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    var contentAlpha by remember { mutableStateOf(0f) }
    LaunchedEffect(scaffoldState) {
        snapshotFlow { scaffoldState.bottomSheetState.progress }
            .collect {
                contentAlpha = it.normalizedFraction
            }
    }
    BackHandler(enabled = scaffoldState.bottomSheetState.isExpanded) {
        coroutineScope.launch {
            scaffoldState.bottomSheetState.animateTo(BottomSheetValue.Collapsed)
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(PlayerBottomSheetPeekHeight)
            .padding(horizontal = 16.dp)
            .alpha(1f - contentAlpha)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) {
                coroutineScope.launch {
                    scaffoldState.bottomSheetState.animateTo(BottomSheetValue.Expanded)
                }
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayingIndicator(
            modifier = Modifier
                .width(20.dp)
                .height(10.dp),
            playing = state.playingState == UiStationPlayingState.PLAYING,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = state.currentStationNameOrEmpty,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        PlayPauseButton(state = state.playingState) {
            if (contentAlpha < 1f) {
                viewModel.onEvent(PlayerBottomSheetEvent.OnPlayPauseClick)
            }
        }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .alpha(contentAlpha)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        val currentStationIndexState = rememberUpdatedState(state.currentStationIndex)
        val pagerState = rememberPagerState(state.currentStationIndex.coerceAtLeast(0))
        LaunchedEffect(pagerState) {
            launch {
                snapshotFlow { pagerState.currentPage }
                    .filter { state.currentStationIndex != it }
                    .collect {
                        viewModel.onEvent(PlayerBottomSheetEvent.OnSelectStation(it))
                    }
            }

            launch {
                snapshotFlow { currentStationIndexState.value }.filter { it >= 0 }.collect {
                    pagerState.animateScrollToPage(it)
                }
            }
        }

        val itemSize = 270.dp
        HorizontalPager(
            count = state.stations.size,
            state = pagerState,
        ) { index ->
            val station = state.stations[index]
            Column(
                modifier = Modifier.graphicsLayer {
                    val pageOffset = calculateCurrentOffsetForPage(index).absoluteValue
                    lerp(
                        start = 0.85f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    ).also { scale ->
                        scaleX = scale
                        scaleY = scale
                    }

                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                }
            ) {
                StationCover(
                    modifier = Modifier.size(itemSize),
                    url = station.image,
                )
                Spacer(modifier = Modifier.height(12.dp))
                TickerTextView(
                    modifier = Modifier.width(itemSize),
                    text = station.name,
                    textSize = 18.sp,
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayBackButton(
                visible = state.hasPreviousStation,
                size = 46.dp,
                icon = Icons.Rounded.SkipPrevious,
                contentDescription = stringResource(R.string.accessibility_previous_station),
                onClick = { viewModel.onEvent(PlayerBottomSheetEvent.PreviousStation) }
            )
            Spacer(Modifier.width(18.dp))
            PlayBackButton(
                visible = true,
                size = 80.dp,
                icon = { size ->
                    when (state.playingState) {
                        UiStationPlayingState.PLAYING -> {
                            Icon(
                                modifier = Modifier.size(size),
                                imageVector = Icons.Rounded.Pause,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = null,
                            )
                        }
                        UiStationPlayingState.NONE -> {
                            Icon(
                                modifier = Modifier.size(size),
                                imageVector = Icons.Rounded.PlayArrow,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = null,
                            )
                        }
                        UiStationPlayingState.BUFFERING -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(size / 2f),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                },
                contentDescription = stringResource(
                    if (state.playing) R.string.accessibility_pause else R.string.accessibility_play,
                ),
                onClick = { viewModel.onEvent(PlayerBottomSheetEvent.OnPlayPauseClick) }
            )
            Spacer(Modifier.width(18.dp))
            PlayBackButton(
                visible = state.hasNextStation,
                size = 46.dp,
                icon = Icons.Rounded.SkipNext,
                contentDescription = stringResource(R.string.accessibility_next_station),
                onClick = { viewModel.onEvent(PlayerBottomSheetEvent.NextStation) }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        ActionsBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(horizontal = 16.dp),
        ) {
            ActionButton(
                icon = state.currentStationInFavorite.select(Icons.Rounded.Favorite, Icons.Rounded.FavoriteBorder),
                contentDescription = state.currentStationInFavorite.stringResource(
                    positiveId = R.string.accessibility_remove_favorite,
                    negativeId = R.string.accessibility_add_favorite
                )
            ) {
                viewModel.onEvent(PlayerBottomSheetEvent.UpdateStationFavorite(!state.currentStationInFavorite))
            }
            Box(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
            )
            ActionButton(
                icon = state.sleepTimerScheduled.select(Icons.Rounded.Timelapse, Icons.Rounded.Timer),
                contentDescription = stringResource(R.string.accessibility_sleep_timer),
                badgeText = state.sleepTimerLeftTimeFormatted,
            ) {
                sleepTimerViewModel.show()
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ActionsBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        content()
    }
}

@Composable
private fun TickerTextView(
    modifier: Modifier = Modifier,
    text: String,
    textSize: TextUnit = 14.sp,
    textColor: Color = Color.Black,
) {
    val textColorInt = textColor.colorInt
    AndroidView(
        modifier = modifier,
        factory = {
            TextView(it).apply {
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.MARQUEE
                marqueeRepeatLimit = -1
                isSelected = true
                isFocusableInTouchMode = true

            }
        },
        update = {
            it.textSize = textSize.value
            it.setTextColor(textColorInt)
            it.text = text
        }
    )
}

@Composable
private fun RowScope.ActionButton(
    icon: ImageVector,
    contentDescription: String,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(onClick = onClick),
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            imageVector = icon,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = contentDescription
        )

        if (!badgeText.isNullOrEmpty()) {
            var badgeWidth by remember { mutableStateOf(0) }
            Badge(
                modifier = Modifier
                    .align(Alignment.Center)
                    .onGloballyPositioned { badgeWidth = it.size.width }
                    .offset(x = (badgeWidth / 2f).toDp() + 4.dp + 12.dp),
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Text(
                    text = badgeText,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
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
    PlayBackButton(
        visible = visible,
        size = size,
        icon = { iconSize ->
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = icon,
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = null,
            )
        },
        contentDescription = contentDescription,
        onClick = onClick,
    )
}

@Composable
private fun PlayBackButton(
    visible: Boolean,
    size: Dp,
    icon: @Composable (Dp) -> Unit,
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
        Box(modifier = Modifier.align(Alignment.Center)) {
            icon((size.value * 0.6).dp)
        }
    }
}


@Composable
fun StationCover(
    modifier: Modifier = Modifier,
    url: String?,
) {
    BoxWithConstraints(modifier = modifier) {
        val maxHeight = maxHeight
        GlideImage(
            modifier = modifier.background(MaterialTheme.colorScheme.secondary),
            imageModel = url,
            loading = { CircularProgressIndicator(Modifier.align(Alignment.Center)) },
            failure = {
                Box(Modifier.fillMaxSize()) {
                    Icon(
                        modifier = Modifier
                            .size(maxHeight * 0.45f)
                            .align(Alignment.Center),
                        contentDescription = stringResource(R.string.accessibility_station_poster),
                        painter = painterResource(R.drawable.ic_radio),
                        tint = Color(0xff907A88),
                    )
                }
            }
        )
    }
}

private const val BAR_WIDTH_FACTOR = 0.2f
private const val BAR_COUNT = 4
private const val BAR_DURATION_MS = 400

@Composable
private fun PlayingIndicator(
    modifier: Modifier = Modifier,
    playing: Boolean
) {
    BoxWithConstraints(modifier = modifier) {
        val maxWidth = this.maxWidth
        val maxHeight = this.maxHeight
        val barWidth = maxWidth * BAR_WIDTH_FACTOR

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            repeat(BAR_COUNT) {
                PlayingIndicatorBar(
                    modifier = Modifier.align(Alignment.Bottom),
                    maxHeight = maxHeight,
                    playing = playing,
                    delay = 100 * it,
                    width = barWidth,
                )
            }
        }
    }
}

private fun cycleEasing(): Easing {
    return Easing { sin(Math.PI * it).toFloat() }
}

@Composable
private fun PlayingIndicatorBar(
    modifier: Modifier = Modifier,
    width: Dp,
    maxHeight: Dp,
    playing: Boolean,
    delay: Int,
) {
    val fraction by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = BAR_DURATION_MS,
                easing = cycleEasing(),
            ),
            initialStartOffset = StartOffset(delay)
        )
    )
    Box(
        modifier
            .width(width)
            .height(if (playing) (maxHeight * fraction).coerceAtLeast(1.dp) else 1.dp)
            .background(MaterialTheme.colorScheme.primary)
    )
}
