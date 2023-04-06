package ru.debajo.srrradio.ui.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class UiMainTile(
    val title: Int,
    val icon: ImageVector,
)
