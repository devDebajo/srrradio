package ru.debajo.srrradio.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController

@Composable
fun SrrradioNavigationHost(content: @Composable (NavTree) -> Unit) {
    val rootNavigationController = rememberNavController()
    val mainNavigationController = rememberNavController()
    val navTree = remember(rootNavigationController, mainNavigationController) {
        NavTree(rootNavigationController, mainNavigationController)
    }
    CompositionLocalProvider(NavTree.Local provides navTree) {
        content(navTree)
    }
}
