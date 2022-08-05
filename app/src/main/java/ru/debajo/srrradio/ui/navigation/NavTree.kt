package ru.debajo.srrradio.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import ru.debajo.srrradio.R

class NavTree(val rootController: NavHostController, mainController: NavHostController) {

    val main: Main = Main(mainController)

    val addCustomStation: Screen = Screen(
        route = "addCustomStation",
        navController = rootController
    )

    val collection: Screen = Screen(
        route = "collection",
        navController = rootController
    )

    val sendLogs: Screen = Screen(
        route = "sendLogs",
        navController = rootController
    )

    class Main(
        override val navController: NavHostController
    ) : AbstractScreen {
        override val route: String = "main"

        val radio: Radio = Radio(navController)

        val settings: NavigationScreen = NavigationScreen(
            route = "settings",
            icon = Icons.Rounded.Settings,
            titleRes = R.string.settings_title,
            navController = navController
        )

        val screens: List<NavigationScreen> = listOf(radio, settings)
    }

    class Radio(navController: NavHostController) : NavigationScreen(
        route = "radio",
        icon = Icons.Rounded.Radio,
        titleRes = R.string.radio_title,
        navController = navController,
    ) {
        val root: Screen = Screen(
            route = "radioRoot",
            navController = navController
        )

        val newStations: Screen = Screen(
            route = "newStations",
            navController = navController
        )

        private val nestedScreens = listOf(newStations).map { it.route }

        override fun navigate() {
            val currentRoute = navController.currentDestination?.route
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    if (currentRoute !in nestedScreens) {
                        saveState = true
                    }
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    interface AbstractScreen {
        val route: String
        val navController: NavController

        fun navigate() {
            navController.navigate(route)
        }
    }

    open class Screen(
        override val route: String,
        override val navController: NavController,
    ) : AbstractScreen

    open class NavigationScreen(
        val icon: ImageVector,
        val titleRes: Int,
        route: String,
        navController: NavController,
    ) : Screen(
        route = route,
        navController = navController
    ) {
        override fun navigate() {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    companion object {
        val Local = staticCompositionLocalOf<NavTree> { TODO() }

        val current: NavTree
            @Composable
            get() = Local.current
    }
}
