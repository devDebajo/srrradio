package ru.debajo.srrradio.ui.host.main.player

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Save
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.skydoves.landscapist.glide.GlideImage
import kotlin.math.absoluteValue
import kotlin.math.sin
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ru.debajo.srrradio.R
import ru.debajo.srrradio.ui.common.CustomText
import ru.debajo.srrradio.ui.ext.darken
import ru.debajo.srrradio.ui.ext.select
import ru.debajo.srrradio.ui.ext.stringResource
import ru.debajo.srrradio.ui.ext.toDp
import ru.debajo.srrradio.ui.host.main.LocalSnackbarLauncher
import ru.debajo.srrradio.ui.host.main.bottomSheetBgColor
import ru.debajo.srrradio.ui.host.main.list.PlayPauseButton
import ru.debajo.srrradio.ui.host.main.player.model.PlayerBottomSheetEvent
import ru.debajo.srrradio.ui.host.main.player.model.PlayerBottomSheetState
import ru.debajo.srrradio.ui.host.main.timer.SleepTimerViewModel
import ru.debajo.srrradio.ui.model.UiStationPlayingState
import ru.debajo.srrradio.ui.navigation.NavTree

val PlayerBottomSheetPeekHeight = 60.dp

@Composable
@OptIn(ExperimentalPagerApi::class, FlowPreview::class)
fun PlayerBottomSheetContent(
    scaffoldState: BottomSheetScaffoldState,
    navigationHeight: Dp,
) {
    val viewModel = PlayerBottomSheetViewModel.Local.current
    val sleepTimerViewModel = SleepTimerViewModel.Local.current
    val state: PlayerBottomSheetState by viewModel.state.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    BackHandler(enabled = scaffoldState.bottomSheetState.isExpanded) {
        coroutineScope.launch {
            scaffoldState.bottomSheetState.collapse()
        }
    }

    BottomSheetHeader(
        state = state,
        visible = scaffoldState.bottomSheetState.isCollapsed,
        onClick = {
            coroutineScope.launch {
                scaffoldState.bottomSheetState.expand()
            }
        },
        onPlayPauseClick = {
            viewModel.onEvent(PlayerBottomSheetEvent.OnPlayPauseClick)
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        val currentStationIndexState = rememberUpdatedState(state.currentStationIndex)
        val pagerState = rememberPagerState(state.currentStationIndex.coerceAtLeast(0))
        LaunchedEffect(pagerState) {
            launch {
                snapshotFlow { pagerState.currentPage }
                    .debounce(100)
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

        val itemSize = if (isHorizontalOrientation()) 100.dp else 270.dp
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
                    modifier = Modifier
                        .size(itemSize)
                        .clip(RoundedCornerShape(10.dp)),
                    url = station.image,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    modifier = Modifier
                        .width(itemSize)
                        .basicMarquee(),
                    text = station.name,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        CustomText(
            modifier = Modifier.width(itemSize),
            text = state.title ?: stringResource(R.string.no_track),
            fontSize = 13.sp,
            lineHeight = 18.sp,
            maxLines = 2,
            minLines = 2,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
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
        Spacer(modifier = Modifier.height(35.dp))
        val snackbarLauncher = LocalSnackbarLauncher.current
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
                if (state.currentStationInFavorite) {
                    snackbarLauncher.show(R.string.station_removed_from_favorite)
                } else {
                    snackbarLauncher.show(R.string.station_added_to_favorite)
                }
                viewModel.onEvent(PlayerBottomSheetEvent.UpdateStationFavorite(!state.currentStationInFavorite))
            }
            ActionButtonDivider()
            ActionButton(
                icon = state.sleepTimerScheduled.select(Icons.Rounded.Timelapse, Icons.Rounded.Timer),
                contentDescription = stringResource(R.string.accessibility_sleep_timer),
                badgeText = state.sleepTimerLeftTimeFormatted,
            ) {
                sleepTimerViewModel.show()
            }

            ActionButtonDivider()
            val navTree = NavTree.current
            ActionButton(
                icon = Icons.Rounded.Equalizer,
                contentDescription = stringResource(R.string.accessibility_equalizer),
            ) {
                navTree.equalizer.navigate()
            }

            if (!state.title.isNullOrEmpty()) {
                ActionButtonDivider()
                ActionButton(
                    icon = Icons.Rounded.Save,
                    contentDescription = stringResource(R.string.accessibility_save_track),
                ) {
                    snackbarLauncher.show(R.string.track_added_to_collection)
                    viewModel.onEvent(PlayerBottomSheetEvent.AddTrackToCollection(state.title.orEmpty()))
                }
            }
        }
        Spacer(modifier = Modifier.height(navigationHeight + 20.dp))
    }
}

@Composable
private fun BottomSheetHeader(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    state: PlayerBottomSheetState,
    onClick: () -> Unit,
    onPlayPauseClick: () -> Unit
) {
    val visibleAsState = rememberUpdatedState(visible)
    var alpha by remember { mutableStateOf(if (visible) 1f else 0f) }
    LaunchedEffect(Unit) {
        snapshotFlow { visibleAsState.value }.collect {
            val targetAlpha = if (it) 1f else 0f
            animate(
                initialValue = alpha,
                targetValue = targetAlpha,
                block = { value, _ -> alpha = value }
            )
        }
    }

    Row(
        modifier = modifier
            .alpha(alpha)
            .fillMaxWidth()
            .height(PlayerBottomSheetPeekHeight)
            .padding(horizontal = 16.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayingIndicator(
            modifier = Modifier
                .width(20.dp)
                .height(10.dp),
            playing = state.playingState == UiStationPlayingState.PLAYING,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = state.currentStationNameOrEmpty,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = state.title ?: stringResource(R.string.no_track),
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        PlayPauseButton(state = state.playingState) {
            if (visible) {
                onPlayPauseClick()
            }
        }
    }
}

@Composable
private fun ActionButtonDivider() {
    Box(
        modifier = Modifier
            .padding(vertical = 5.dp)
            .width(1.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

@Composable
private fun ActionsBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bottomSheetBgColor.darken(0.3f))
    ) {
        content()
    }
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
            contentScale = ContentScale.Fit,
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
                        tint = MaterialTheme.colorScheme.onSecondary,
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

@Composable
fun isHorizontalOrientation(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}
