@file:OptIn(ExperimentalSnapperApi::class)

package ru.debajo.srrradio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import ru.debajo.reduktor.lazyViewModel
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.ui.list.StationsList
import ru.debajo.srrradio.ui.list.StationsListViewModel
import ru.debajo.srrradio.ui.player.PlayerBottomSheetContent
import ru.debajo.srrradio.ui.player.PlayerBottomSheetViewModel
import ru.debajo.srrradio.ui.theme.SrrradioTheme

class MainActivity : ComponentActivity() {

    private val stationsListViewModel: StationsListViewModel by lazyViewModel { AppApiHolder.get().stationsListViewModel }
    private val playerBottomSheetViewModel: PlayerBottomSheetViewModel by lazyViewModel { AppApiHolder.get().playerBottomSheetViewModel }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(
                StationsListViewModel.Local provides stationsListViewModel,
                PlayerBottomSheetViewModel.Local provides playerBottomSheetViewModel,
            ) {
                SrrradioTheme {
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
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun MainScreen() {
    val bottomSheetViewModel = PlayerBottomSheetViewModel.Local.current
    val bottomSheetState by bottomSheetViewModel.state.collectAsState()

    val showBottomSheet = bottomSheetState.showBottomSheet
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colorScheme.background,
        sheetBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        content = { StationsList() },
        sheetPeekHeight = if (showBottomSheet) 60.dp else 0.dp,
        sheetContent = {
            if (showBottomSheet) {
                PlayerBottomSheetContent(scaffoldState = scaffoldState)
            }
        },
    )
}
