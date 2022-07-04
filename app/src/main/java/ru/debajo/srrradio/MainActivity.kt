package ru.debajo.srrradio

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.launch
import ru.debajo.reduktor.lazyViewModel
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.ui.ext.colorInt
import ru.debajo.srrradio.ui.list.StationsList
import ru.debajo.srrradio.ui.list.StationsListViewModel
import ru.debajo.srrradio.ui.player.PlayerBottomSheetContent
import ru.debajo.srrradio.ui.player.PlayerBottomSheetPeekHeight
import ru.debajo.srrradio.ui.player.PlayerBottomSheetViewModel
import ru.debajo.srrradio.ui.theme.SrrradioTheme

class MainActivity : ComponentActivity() {

    private val stationsListViewModel: StationsListViewModel by lazyViewModel { AppApiHolder.get().stationsListViewModel }
    private val playerBottomSheetViewModel: PlayerBottomSheetViewModel by lazyViewModel { AppApiHolder.get().playerBottomSheetViewModel }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

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
        val navigationColor = rememberUpdatedState(MaterialTheme.colorScheme.secondaryContainer)
        LaunchedEffect(Unit) {
            snapshotFlow { navigationColor.value }.collect {
                window.navigationBarColor = it.colorInt
            }
        }
    }
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

    BottomSheetScaffold(
        modifier = Modifier.systemBarsPadding(),
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colorScheme.background,
        sheetBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
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
        sheetPeekHeight = if (showBottomSheet) PlayerBottomSheetPeekHeight else 0.dp,
        sheetContent = {
            if (showBottomSheet) {
                PlayerBottomSheetContent(scaffoldState = scaffoldState)
            }
        },
    )
}
