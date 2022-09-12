package ru.debajo.srrradio.ui.host

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.debajo.srrradio.R
import ru.debajo.srrradio.auth.AuthManagerProvider
import ru.debajo.srrradio.common.utils.getFromDi
import ru.debajo.srrradio.common.utils.inject
import ru.debajo.srrradio.di.diViewModels
import ru.debajo.srrradio.media.StationCoverLoader
import ru.debajo.srrradio.rate.RateAppManager
import ru.debajo.srrradio.ui.common.SnowFall
import ru.debajo.srrradio.ui.common.SnowFallUseCase
import ru.debajo.srrradio.ui.common.alert.AlertDialogHost
import ru.debajo.srrradio.ui.common.alert.AlertDialogState
import ru.debajo.srrradio.ui.common.alert.LocalAlertDialogState
import ru.debajo.srrradio.ui.ext.AndroidColor
import ru.debajo.srrradio.ui.ext.colorInt
import ru.debajo.srrradio.ui.ext.rememberFixedHapticFeedback
import ru.debajo.srrradio.ui.host.add.AddCustomStationScreen
import ru.debajo.srrradio.ui.host.collection.CollectionScreen
import ru.debajo.srrradio.ui.host.main.LocalSnackbarLauncher
import ru.debajo.srrradio.ui.host.main.MainScreen
import ru.debajo.srrradio.ui.host.main.bottomSheetBgColor
import ru.debajo.srrradio.ui.host.main.list.StationsListViewModel
import ru.debajo.srrradio.ui.host.main.player.PlayerBottomSheetViewModel
import ru.debajo.srrradio.ui.host.main.rememberSnackbarLauncher
import ru.debajo.srrradio.ui.host.main.settings.SettingsViewModel
import ru.debajo.srrradio.ui.host.main.timer.SleepTimerViewModel
import ru.debajo.srrradio.ui.navigation.SrrradioNavigationHost
import ru.debajo.srrradio.ui.theme.SrrradioTheme
import ru.debajo.srrradio.ui.theme.SrrradioThemeManager

class HostActivity : ComponentActivity() {

    private val stationsListViewModel: StationsListViewModel by diViewModels()
    private val playerBottomSheetViewModel: PlayerBottomSheetViewModel by diViewModels()
    private val sleepTimerViewModel: SleepTimerViewModel by diViewModels()
    private val settingsViewModel: SettingsViewModel by diViewModels()
    private val themeManager: SrrradioThemeManager by inject()
    private val authManagerProvider: AuthManagerProvider by inject()
    private val snowFallUseCase: SnowFallUseCase by inject()
    private val rateAppManager: RateAppManager by inject()
    private val alertDialogState: AlertDialogState by lazy { AlertDialogState(this@HostActivity) }

    private val openDocumentLauncher: ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
        if (it != null) {
            settingsViewModel.onFileSelected(it.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            handleRateApp()
        }
        lifecycleScope.launch {
            authManagerProvider().setActivity(this@HostActivity)
        }
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
                SettingsViewModel.Local provides settingsViewModel,
                SrrradioThemeManager.Local provides themeManager,
                LocalIndication provides rememberRipple(),
                LocalOpenDocumentLauncher provides openDocumentLauncher,
                LocalHapticFeedback provides rememberFixedHapticFeedback()
            ) {
                SrrradioTheme {
                    ConfigureNavigationColor()
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val snackbarLauncher = rememberSnackbarLauncher()
                        CompositionLocalProvider(
                            LocalSnackbarLauncher provides snackbarLauncher,
                            LocalAlertDialogState provides alertDialogState,
                        ) {
                            Box {
                                HostScreen()
                                SnackbarHost(
                                    hostState = snackbarLauncher.snackbarHostState,
                                    modifier = Modifier
                                        .systemBarsPadding()
                                        .align(Alignment.BottomCenter),
                                )
                                AlertDialogHost(state = alertDialogState)

                                val snowFallEnabled by snowFallUseCase.enabled.collectAsState()
                                if (snowFallEnabled) {
                                    SnowFall(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleRateApp() {
        rateAppManager.hostActivityCreated()
        val actions = rateAppManager.getRateActions()
        if (actions.size != 2) {
            return
        }

        val cancelAction = actions[0]
        val confirmAction = actions[1]

        alertDialogState.alert(
            title = R.string.rate_app_title,
            content = R.string.rate_app_message,
            confirm = confirmAction.res,
            dismiss = cancelAction.res,
            onDismiss = { rateAppManager.onRateAction(this, cancelAction) },
            onConfirm = { rateAppManager.onRateAction(this, confirmAction) },
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        lifecycleScope.launch {
            authManagerProvider().onActivityResult(requestCode, data)
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

        val stationCoverLoader: StationCoverLoader = remember { getFromDi() }
        val secondaryColor = rememberUpdatedState(MaterialTheme.colorScheme.secondary)
        val onSecondaryColor = rememberUpdatedState(MaterialTheme.colorScheme.onSecondary)
        LaunchedEffect(Unit) {
            combine(
                snapshotFlow { secondaryColor.value },
                snapshotFlow { onSecondaryColor.value }
            ) { a, b -> a to b }.collect { (secondary, onSecondary) ->
                stationCoverLoader.setColors(secondary.colorInt, onSecondary.colorInt)
            }
        }
    }

    @Composable
    private fun HostScreen() {
        SrrradioNavigationHost { navTree ->
            NavHost(navTree.rootController, startDestination = navTree.main.route) {
                composable(navTree.main.route) {
                    MainScreen()
                }

                composable(navTree.addCustomStation.route) {
                    AddCustomStationScreen()
                }

                composable(navTree.collection.route) {
                    CollectionScreen()
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

val LocalOpenDocumentLauncher = staticCompositionLocalOf<ActivityResultLauncher<Array<String>>> { TODO() }
