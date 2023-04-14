package ru.debajo.srrradio.ui.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
sealed interface UiMainTile {

    val title: Int
    val icon: ImageVector
    val regular: Regular
    val clickable: Boolean

    @Immutable
    data class Regular(
        override val title: Int,
        override val icon: ImageVector
    ) : UiMainTile {
        override val regular: Regular = this
        override val clickable: Boolean = true
    }

    @Immutable
    data class Progress(
        val progress: Float,
        val loading: Boolean,
        override val regular: Regular,
    ) : UiMainTile {
        override val title: Int = regular.title
        override val icon: ImageVector = regular.icon
        override val clickable: Boolean = !loading
    }
}
