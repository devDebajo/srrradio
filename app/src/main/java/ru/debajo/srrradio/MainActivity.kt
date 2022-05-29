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
import ru.debajo.srrradio.common.presentation.viewModelsBy
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.ui.list.StationsList
import ru.debajo.srrradio.ui.list.StationsListViewModel
import ru.debajo.srrradio.ui.list.reduktor.StationsListState
import ru.debajo.srrradio.ui.player.PlayerContent
import ru.debajo.srrradio.ui.theme.SrrradioTheme

class MainActivity : ComponentActivity() {

    private val stationsListViewModel: StationsListViewModel by viewModelsBy { AppApiHolder.get().stationsListViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(
                StationsListViewModel.Local provides stationsListViewModel,
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
    val viewModel = StationsListViewModel.Local.current
    val state by viewModel.state.collectAsState()
    val dataState = state as? StationsListState.Data ?: return
    val playerState = dataState.playerState as? RadioPlayer.State.HasStation
    val showBottomSheet = playerState != null
    val scaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colorScheme.background,
        sheetBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        content = { StationsList() },
        sheetPeekHeight = if (showBottomSheet) 60.dp else 0.dp,
        sheetContent = {
            if (playerState != null) {
                PlayerContent(
                    playerState = playerState,
                    scaffoldState = scaffoldState,
                    playlist = dataState.stations,
                    currentStationIndex = dataState.currentStationIndex,
                )
            }
        },
    )
}
