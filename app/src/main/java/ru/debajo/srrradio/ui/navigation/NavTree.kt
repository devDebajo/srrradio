package ru.debajo.srrradio.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import ru.debajo.srrradio.R

class NavTree(rootController: NavHostController, mainController: NavHostController) {

    val main: Main = Main(mainController)
    val host: Host = Host(rootController)

    class Main(val navController: NavHostController) {
        val radio: Screen = Screen(
            route = "radio",
            icon = Icons.Rounded.Radio,
            titleRes = R.string.radio_title,
            navController = navController
        )

        val settings: Screen = Screen(
            route = "settings",
            icon = Icons.Rounded.Settings,
            titleRes = R.string.settings_title,
            navController = navController
        )

        val screens = listOf(radio, settings)
    }

    class Host(val navController: NavHostController) {
        val main: Screen = Screen(
            route = "main",
            icon = Icons.Rounded.Navigation,
            titleRes = R.string.radio_title,
            navController = navController
        )

        val addCustomStation: Screen = Screen(
            route = "addCustomStation",
            icon = Icons.Rounded.Add,
            titleRes = R.string.radio_title,
            navController = navController
        )
    }

    class Screen(
        val route: String,
        val icon: ImageVector,
        val titleRes: Int,
        private val navController: NavController,
    ) {
        fun navigate() {
            navController.navigate(route)
        }
    }

    companion object {
        val Local = staticCompositionLocalOf<NavTree> { TODO() }

        val current: NavTree
            @Composable
            get() = Local.current
    }
}

