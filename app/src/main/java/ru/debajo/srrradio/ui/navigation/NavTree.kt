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
import ru.debajo.srrradio.auth.log

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

    val equalizer: Screen = Screen(
        route = "equalizer",
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
        val root: Screen = childScreen(route = "radioRoot")
        val newStations: Screen = childScreen(route = "newStations")
        val recommendedStations: Screen = childScreen(route = "recommendedStations")
        val popularStations: Screen = childScreen(route = "popularStations")
        val favoriteStations: Screen = childScreen(route = "favoriteStations")
        val stationsOnMap: Screen = childScreen(route = "stationsOnMap")

        override val nestedScreens: List<Screen> = listOf(
            newStations,
            recommendedStations,
            popularStations,
            favoriteStations,
            stationsOnMap,
        )
    }

    interface AbstractScreen {
        val route: String
        val navController: NavController

        fun navigate() {
            log("navigate_to", "route" to route)
            navController.navigate(route)
        }
    }

    open class Screen(
        override val route: String,
        override val navController: NavController,
    ) : AbstractScreen {
        protected fun childScreen(route: String): Screen {
            return Screen(route, navController)
        }
    }

    open class NavigationScreen(
        val icon: ImageVector,
        val titleRes: Int,
        route: String,
        navController: NavController,
    ) : Screen(
        route = route,
        navController = navController
    ) {
        open val nestedScreens: List<Screen> = emptyList()

        override fun navigate() {
            log("navigate_to", "route" to route)
            val currentRoute = navController.currentDestination?.route
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    if (currentRoute !in nestedScreens.map { it.route }) {
                        saveState = true
                    }
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
