package ru.debajo.srrradio

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.launch
import ru.debajo.reduktor.lazyViewModel
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.ui.ext.colorInt
import ru.debajo.srrradio.ui.ext.toDp
import ru.debajo.srrradio.ui.list.StationsList
import ru.debajo.srrradio.ui.list.StationsListViewModel
import ru.debajo.srrradio.ui.player.PlayerBottomSheetContent
import ru.debajo.srrradio.ui.player.PlayerBottomSheetPeekHeight
import ru.debajo.srrradio.ui.player.PlayerBottomSheetViewModel
import ru.debajo.srrradio.ui.player.normalizedFraction
import ru.debajo.srrradio.ui.theme.SrrradioTheme

typealias AndroidColor = android.graphics.Color

class MainActivity : ComponentActivity() {

    private val stationsListViewModel: StationsListViewModel by lazyViewModel { AppApiHolder.get().stationsListViewModel }
    private val playerBottomSheetViewModel: PlayerBottomSheetViewModel by lazyViewModel { AppApiHolder.get().playerBottomSheetViewModel }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT

        setContent {
            CompositionLocalProvider(
                StationsListViewModel.Local provides stationsListViewModel,
                PlayerBottomSheetViewModel.Local provides playerBottomSheetViewModel,
            ) {
                SrrradioTheme {
                    ConfigureNavigationColor()
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen()
                    }
                }
            }
        }
    }

    @Composable
    private fun ConfigureNavigationColor() {
        val navigationColor = rememberUpdatedState(bottomSheetBgColor())
        LaunchedEffect(Unit) {
            snapshotFlow { navigationColor.value }.collect {
                window.navigationBarColor = it.colorInt
            }
        }
    }
}

@Composable
private fun bottomSheetBgColor(): Color {
    return MaterialTheme.colorScheme.secondaryContainer
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun MainScreen() {
    val bottomSheetViewModel = PlayerBottomSheetViewModel.Local.current
    val bottomSheetState by bottomSheetViewModel.state.collectAsState()

    val showBottomSheet = bottomSheetState.showBottomSheet
    val bottomSheetScaffoldState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetScaffoldState)
    val coroutineScope = rememberCoroutineScope()
    var navigationHeight by remember { mutableStateOf(0) }
    var navigationOffset by remember { mutableStateOf(0f) }

    LaunchedEffect(bottomSheetScaffoldState) {
        snapshotFlow { bottomSheetScaffoldState.progress }.collect {
            val fraction = it.normalizedFraction
            navigationOffset = navigationHeight * fraction
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            backgroundColor = MaterialTheme.colorScheme.background,
            sheetBackgroundColor = bottomSheetBgColor(),
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            content = {
                StationsList {
                    if (bottomSheetScaffoldState.isExpanded) {
                        coroutineScope.launch {
                            bottomSheetScaffoldState.animateTo(BottomSheetValue.Collapsed)
                        }
                    }
                }
            },
            sheetPeekHeight = if (showBottomSheet) PlayerBottomSheetPeekHeight + navigationHeight.toDp() else 0.dp,
            sheetContent = {
                if (showBottomSheet) {
                    PlayerBottomSheetContent(scaffoldState = scaffoldState)
                }
            },
        )

        Navigation(modifier = Modifier
            .align(Alignment.BottomCenter)
            .onGloballyPositioned { navigationHeight = it.size.height }
            .offset(y = navigationOffset.toDp())
        )
    }
}

@Composable
private fun Navigation(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.secondary)
        )
        NavigationBar(
            containerColor = bottomSheetBgColor()
        ) {
            NavigationBarItem(
                selected = true,
                onClick = {},
                icon = {
                    Icon(
                        Icons.Rounded.Radio,
                        contentDescription = stringResource(R.string.radio_title)
                    )
                },
                label = { Text(stringResource(R.string.radio_title)) }
            )

            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = {
                    Icon(
                        Icons.Rounded.Settings,
                        contentDescription = stringResource(R.string.settings_title)
                    )
                },
                label = { Text(stringResource(R.string.settings_title)) }
            )
        }
    }
}
