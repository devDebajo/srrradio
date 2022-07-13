package ru.debajo.srrradio

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.reduktor.lazyViewModel
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.ui.ext.colorInt
import ru.debajo.srrradio.ui.ext.select
import ru.debajo.srrradio.ui.ext.toDp
import ru.debajo.srrradio.ui.list.StationsList
import ru.debajo.srrradio.ui.list.StationsListViewModel
import ru.debajo.srrradio.ui.player.PlayerBottomSheetContent
import ru.debajo.srrradio.ui.player.PlayerBottomSheetPeekHeight
import ru.debajo.srrradio.ui.player.PlayerBottomSheetViewModel
import ru.debajo.srrradio.ui.player.normalizedFraction
import ru.debajo.srrradio.ui.settings.SettingsScreen
import ru.debajo.srrradio.ui.theme.SrrradioTheme
import ru.debajo.srrradio.ui.timer.SleepTimerBottomSheet
import ru.debajo.srrradio.ui.timer.SleepTimerViewModel

typealias AndroidColor = android.graphics.Color

class MainActivity : ComponentActivity() {

    private val stationsListViewModel: StationsListViewModel by lazyViewModel { AppApiHolder.get().stationsListViewModel }
    private val playerBottomSheetViewModel: PlayerBottomSheetViewModel by lazyViewModel { AppApiHolder.get().playerBottomSheetViewModel }
    private val sleepTimerViewModel: SleepTimerViewModel by lazyViewModel { AppApiHolder.get().sleepTimerViewModel }

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
                SleepTimerViewModel.Local provides sleepTimerViewModel,
                LocalIndication provides rememberRipple(),
            ) {
                SrrradioTheme(useDarkTheme = true) {
                    ConfigureNavigationColor()
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreenWithSleepBottomSheet()
                    }
                }
            }
        }
    }

    @Composable
    private fun ConfigureNavigationColor() {
        val navigationColor = rememberUpdatedState(bottomSheetBgColor)
        LaunchedEffect(Unit) {
            snapshotFlow { navigationColor.value }.collect {
                window.navigationBarColor = it.colorInt
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}

private val bottomSheetBgColor: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSecondary

sealed interface NavTree {

    val route: String
    val icon: ImageVector
    val titleRes: Int

    object Radio : NavTree {
        override val route: String = "radio"
        override val icon: ImageVector = Icons.Rounded.Radio
        override val titleRes: Int = R.string.radio_title
    }

    object Settings : NavTree {
        override val route: String = "settings"
        override val icon: ImageVector = Icons.Rounded.Settings
        override val titleRes: Int = R.string.settings_title
    }

    companion object {
        val screens: List<NavTree> = listOf(Radio, Settings)
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun MainScreenWithSleepBottomSheet() {
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
        sheetContent = { SleepTimerBottomSheet() }
    ) {
        MainScreen()
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
    var navigationHeight by remember { mutableStateOf(0) }
    var navigationOffset by remember { mutableStateOf(0f) }
    val navigationController = rememberNavController()

    LaunchedEffect(bottomSheetScaffoldState) {
        snapshotFlow { bottomSheetScaffoldState.progress }.collect {
            val fraction = it.normalizedFraction
            navigationOffset = navigationHeight * fraction
        }
    }

    Box(Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            backgroundColor = MaterialTheme.colorScheme.surface,
            sheetBackgroundColor = bottomSheetBgColor,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            content = {
                Box(Modifier.fillMaxSize()) {
                    NavHost(navigationController, startDestination = NavTree.Radio.route) {
                        composable(NavTree.Radio.route) {
                            StationsList {
                                if (bottomSheetScaffoldState.isExpanded) {
                                    coroutineScope.launch {
                                        bottomSheetScaffoldState.animateTo(BottomSheetValue.Collapsed)
                                    }
                                }
                            }
                        }

                        composable(NavTree.Settings.route) {
                            SettingsScreen()
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

        Navigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .onGloballyPositioned { navigationHeight = it.size.height }
                .offset(y = navigationOffset.toDp()),
            navigationController = navigationController
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
                .background(MaterialTheme.colorScheme.secondary)
        )
        NavigationBar(
            containerColor = bottomSheetBgColor
        ) {
            val navBackStackEntry by navigationController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            for (screen in NavTree.screens) {
                val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                NavigationBarItem(
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primary,
                    ),
                    selected = selected,
                    onClick = {
                        navigationController.navigate(screen.route) {
                            popUpTo(navigationController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = stringResource(screen.titleRes),
                            tint = if (selected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.secondary
                            }
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(screen.titleRes),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                )
            }
        }
    }
}
