package ru.debajo.srrradio.ui.host

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.debajo.reduktor.lazyViewModel
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.ui.ext.AndroidColor
import ru.debajo.srrradio.ui.ext.colorInt
import ru.debajo.srrradio.ui.host.add.AddCustomStationScreen
import ru.debajo.srrradio.ui.host.main.MainScreen
import ru.debajo.srrradio.ui.host.main.bottomSheetBgColor
import ru.debajo.srrradio.ui.host.main.list.StationsListViewModel
import ru.debajo.srrradio.ui.host.main.player.PlayerBottomSheetViewModel
import ru.debajo.srrradio.ui.host.main.timer.SleepTimerViewModel
import ru.debajo.srrradio.ui.navigation.SrrradioNavigationHost
import ru.debajo.srrradio.ui.theme.SrrradioTheme

class HostActivity : ComponentActivity() {

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
                SrrradioTheme {
                    ConfigureNavigationColor()
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        HostScreen()
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

    @Composable
    private fun HostScreen() {
        SrrradioNavigationHost { navTree ->
            NavHost(navTree.host.navController, startDestination = navTree.host.main.route) {
                composable(navTree.host.main.route) {
                    MainScreen()
                }

                composable(navTree.host.addCustomStation.route) {
                    AddCustomStationScreen()
                }
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, HostActivity::class.java)
        }
    }
}
