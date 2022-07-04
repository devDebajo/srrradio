package ru.debajo.srrradio.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ru.debajo.srrradio.R
import ru.debajo.srrradio.ui.model.UiStationPlayingState
import ru.debajo.srrradio.ui.station.PlayPauseButton
import kotlin.math.absoluteValue

val PlayerBottomSheetPeekHeight = 60.dp

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun PlayerBottomSheetContent(scaffoldState: BottomSheetScaffoldState) {
    val viewModel = PlayerBottomSheetViewModel.Local.current
    val state: PlayerBottomSheetState by viewModel.state.collectAsState()

    var contentAlpha by remember { mutableStateOf(0f) }
    LaunchedEffect(scaffoldState) {
        snapshotFlow { scaffoldState.bottomSheetState.progress }
            .collect {
                contentAlpha = when {
                    it.from == BottomSheetValue.Collapsed && it.to == BottomSheetValue.Collapsed -> 0f
                    it.from == BottomSheetValue.Expanded && it.to == BottomSheetValue.Expanded -> 1f
                    it.from == BottomSheetValue.Expanded && it.to == BottomSheetValue.Collapsed -> 1f - it.fraction
                    else -> it.fraction
                }
            }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(PlayerBottomSheetPeekHeight)
            .padding(horizontal = 16.dp)
            .alpha(1f - contentAlpha),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = state.currentStationNameOrEmpty,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        PlayPauseButton(state = state.playingState) {
            viewModel.onEvent(PlayerBottomSheetEvent.OnPlayPauseClick)
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
            StationCover(
                modifier = Modifier
                    .size(itemSize)
                    .graphicsLayer {
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
                    },
                url = station.image,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(state.currentStationNameOrEmpty)
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayBackButton(
                visible = state.hasPreviousStation,
                size = 46.dp,
                icon = Icons.Rounded.SkipPrevious,
                contentDescription = "Прошлая радиостанция",
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
                contentDescription = if (state.playing) "Пауза" else "Продолжить воспроизведение",
                onClick = { viewModel.onEvent(PlayerBottomSheetEvent.OnPlayPauseClick) }
            )
            Spacer(Modifier.width(18.dp))
            PlayBackButton(
                visible = state.hasNextStation,
                size = 46.dp,
                icon = Icons.Rounded.SkipNext,
                contentDescription = "Следующая радиостанция",
                onClick = { viewModel.onEvent(PlayerBottomSheetEvent.NextStation) }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
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
                        contentDescription = null,
                        painter = painterResource(R.drawable.ic_radio),
                        tint = Color(0xff907A88),
                    )
                }
            }
        )
    }
}
