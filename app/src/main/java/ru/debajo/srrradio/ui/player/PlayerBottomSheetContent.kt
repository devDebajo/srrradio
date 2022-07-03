package ru.debajo.srrradio.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.glide.GlideImage
import dev.chrisbanes.snapper.*
import ru.debajo.srrradio.R
import ru.debajo.srrradio.ui.station.PlayPauseButton

@Composable
@ExperimentalSnapperApi
@ExperimentalMaterialApi
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
            .height(60.dp)
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
        val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = state.currentStationIndex)
        val layoutInfo = rememberLazyListSnapperLayoutInfo(
            lazyListState = lazyListState,
        )
        val flingBehavior = rememberSnapperFlingBehavior(
            lazyListState = lazyListState,
            snapOffsetForItem = SnapOffsets.Center,
            springAnimationSpec = SnapperFlingBehaviorDefaults.SpringAnimationSpec,
            endContentPadding = 0.dp,
            snapIndex = { _, startIndex, targetIndex ->
                if (targetIndex > startIndex) startIndex + 1 else startIndex - 1
            },
        )
        LaunchedEffect(layoutInfo) {
//            launch {
//                snapshotFlow { layoutInfo.currentItem?.offset }.collect {
//                    Timber.d("yopta currentItem?.offset ${it}")
//                }
//            }

//            combine(
//                snapshotFlow { layoutInfo.currentItem?.index }.filterNotNull().distinctUntilChanged(),
//                snapshotFlow { lazyListState.isScrollInProgress }
//            ) { currentItem, isScrollInProgress -> currentItem to isScrollInProgress }
//                .filter { (_, isScrollInProgress) -> !isScrollInProgress }
//                .map { (currentItem, _) -> currentItem }
//                .filter { it != currentStationIndex }
//                .mapNotNull { playlist.getOrNull(it - 1) }
//                .collect { station ->
//                    viewModel.onEvent(PlayerBottomSheetEvent.ChangeStation(station, playerState.playing))
//                }
        }
        var textStartPadding by remember { mutableStateOf(0.dp) }
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val maxWidth = maxWidth
            val itemSize = 270.dp
            val spacerSize = (maxWidth - itemSize) / 2f
            textStartPadding = spacerSize

            LazyRow(
                state = lazyListState,
                flingBehavior = flingBehavior,
            ) {
                items(
                    count = state.stations.size + 2,
                ) {
                    if (it == 0 || it == state.stations.size + 1) {
                        Spacer(Modifier.width(spacerSize))
                    } else {
                        val station = state.stations[it - 1]
                        StationCover(
                            modifier = Modifier.size(itemSize),
                            url = station.image,
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            modifier = Modifier.padding(horizontal = textStartPadding),
            text = state.currentStationNameOrEmpty
        )
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
                icon = if (state.playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
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
        Icon(
            modifier = Modifier
                .size((size.value * 0.6).dp)
                .align(Alignment.Center),
            imageVector = icon,
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = null,
        )
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
