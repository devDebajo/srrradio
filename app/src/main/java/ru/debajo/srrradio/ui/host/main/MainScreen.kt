package ru.debajo.srrradio.ui.host.main

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.srrradio.R
import ru.debajo.srrradio.ui.ext.darken
import ru.debajo.srrradio.ui.ext.select
import ru.debajo.srrradio.ui.ext.toDp
import ru.debajo.srrradio.ui.host.main.list.StationsList
import ru.debajo.srrradio.ui.host.main.map.StationsOnMapScreen
import ru.debajo.srrradio.ui.host.main.player.PlayerBottomSheetContent
import ru.debajo.srrradio.ui.host.main.player.PlayerBottomSheetPeekHeight
import ru.debajo.srrradio.ui.host.main.player.PlayerBottomSheetViewModel
import ru.debajo.srrradio.ui.host.main.playlist.DefaultPlaylistScreen
import ru.debajo.srrradio.ui.host.main.playlist.DefaultPlaylistScreenStrategy
import ru.debajo.srrradio.ui.host.main.settings.SettingsScreen
import ru.debajo.srrradio.ui.host.main.timer.SleepTimerBottomSheet
import ru.debajo.srrradio.ui.host.main.timer.SleepTimerViewModel
import ru.debajo.srrradio.ui.navigation.NavTree

val bottomSheetBgColor: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSecondary.darken(0.1f)

@Composable
fun MainScreen() {
    val sleepTimerViewModel = SleepTimerViewModel.Local.current
    val state by sleepTimerViewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(
        state.displaying.select(ModalBottomSheetValue.Expanded, ModalBottomSheetValue.Hidden)
    )

    LaunchedEffect(sheetState) {
        launch {
            sleepTimerViewModel.state.map { it.displaying }.collect {
                if (sheetState.isVisible != it) {
                    if (it) {
                        sheetState.show()
                    } else {
                        sheetState.hide()
                    }
                }
            }
        }

        launch {
            snapshotFlow { sheetState.isVisible }.collect {
                sleepTimerViewModel.onVisibleChanged(it)
            }
        }
    }

    ModalBottomSheetLayout(
        modifier = Modifier.systemBarsPadding(),
        sheetState = sheetState,
        sheetBackgroundColor = bottomSheetBgColor,
        sheetElevation = 20.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContent = { SleepTimerBottomSheet() },
        content = { RadioScreenContent() },
    )
}

val LocalSnackbarLauncher = staticCompositionLocalOf<SnackbarLauncher> { TODO() }

@Composable
fun rememberSnackbarLauncher(): SnackbarLauncher {
    val coroutineScope = rememberCoroutineScope()
    val state = remember { SnackbarHostState() }
    val context = LocalContext.current
    return remember(coroutineScope, state, context) { SnackbarLauncher(coroutineScope, context, state) }
}

class SnackbarLauncher(
    private val coroutineScope: CoroutineScope,
    private val context: Context,
    val snackbarHostState: SnackbarHostState
) {
    private var job: Job? = null

    fun show(messageRes: Int) {
        val message = context.getString(messageRes)
        show(message)
    }

    fun show(message: String) {
        job?.cancel()
        job = coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
}

@Composable
private fun RadioScreenContent() {
    val bottomSheetViewModel = PlayerBottomSheetViewModel.Local.current
    val bottomSheetState by bottomSheetViewModel.state.collectAsState()

    val showBottomSheet = bottomSheetState.showBottomSheet
    val bottomSheetScaffoldState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetScaffoldState)
    val coroutineScope = rememberCoroutineScope()
    var navigationHeight by remember { mutableStateOf(0) }

    val density = LocalDensity.current
    val listBottomPadding by remember {
        derivedStateOf {
            navigationHeight.toDp(density) + PlayerBottomSheetPeekHeight + 12.dp
        }
    }

    val navTree = NavTree.current
    Box(Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            backgroundColor = MaterialTheme.colorScheme.surface,
            sheetElevation = 1.dp,
            sheetBackgroundColor = bottomSheetBgColor,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            content = {
                Box(Modifier.fillMaxSize()) {
                    NavHost(navTree.main.navController, startDestination = navTree.main.radio.route) {
                        navigation(startDestination = navTree.main.radio.root.route, route = navTree.main.radio.route) {
                            composable(navTree.main.radio.root.route) {
                                StationsList(listBottomPadding) {
                                    if (bottomSheetScaffoldState.isExpanded && !bottomSheetScaffoldState.isCollapsed) {
                                        coroutineScope.launch {
                                            bottomSheetScaffoldState.collapse()
                                        }
                                    }
                                }
                            }

                            composable(navTree.main.radio.newStations.route) {
                                DefaultPlaylistScreen(
                                    listBottomPadding = listBottomPadding,
                                    strategy = DefaultPlaylistScreenStrategy.NEW
                                )
                            }

                            composable(navTree.main.radio.popularStations.route) {
                                DefaultPlaylistScreen(
                                    listBottomPadding = listBottomPadding,
                                    strategy = DefaultPlaylistScreenStrategy.POPULAR
                                )
                            }

                            composable(navTree.main.radio.favoriteStations.route) {
                                DefaultPlaylistScreen(
                                    listBottomPadding = listBottomPadding,
                                    strategy = DefaultPlaylistScreenStrategy.FAVORITE
                                )
                            }

                            composable(navTree.main.radio.recommendedStations.route) {
                                DefaultPlaylistScreen(
                                    listBottomPadding = listBottomPadding,
                                    strategy = DefaultPlaylistScreenStrategy.RECOMMENDATIONS
                                )
                            }

                            composable(navTree.main.radio.stationsOnMap.route) {
                                StationsOnMapScreen()
                            }
                        }

                        composable(navTree.main.settings.route) {
                            SettingsScreen(listBottomPadding)
                        }

                        composable(navTree.main.settings.route) {
                            SettingsScreen(listBottomPadding)
                        }
                    }
                }
            },
            sheetPeekHeight = if (showBottomSheet) PlayerBottomSheetPeekHeight + navigationHeight.toDp() else 0.dp,
            sheetContent = {
                if (showBottomSheet) {
                    PlayerBottomSheetContent(
                        scaffoldState = scaffoldState,
                        navigationHeight = navigationHeight.toDp(),
                    )
                } else {
                    Box(Modifier.fillMaxWidth())
                }
            },
        )

        Navigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .onGloballyPositioned { navigationHeight = it.size.height },
            navigationController = navTree.main.navController
        )
    }
}

@Composable
private fun Navigation(
    modifier: Modifier = Modifier,
    navigationController: NavHostController
) {
    Column(modifier = modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
        BottomAppBar(
            containerColor = bottomSheetBgColor,
            floatingActionButton = {
                val navTree = NavTree.current
                FloatingActionButton(
                    onClick = { navTree.addCustomStation.navigate() },
                    containerColor = FloatingActionButtonDefaults.containerColor,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.accessibility_add_custom_station)
                    )
                }
            },
            actions = {
                val navBackStackEntry by navigationController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val screens = NavTree.current.main.screens
                for (screen in screens) {
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    IconButton(onClick = { screen.navigate() }) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = stringResource(screen.titleRes),
                            tint = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                LocalContentColor.current
                            }
                        )
                    }
                }
            }
        )
    }
}
