package ru.debajo.srrradio.ui.host.main.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun SettingsGroup(
    title: String,
    state: SettingsGroupState,
    onHeaderClick: () -> Unit,
    block: @Composable () -> Unit
) {
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column {
            SettingsText(
                alpha = if (state == SettingsGroupState.HALF_ALPHA) 0.2f else 1f,
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                onClick = onHeaderClick,
            )
            AnimatedVisibility(visible = state == SettingsGroupState.EXPANDED) {
                Column {
                    block()
                }
            }
        }
    }
}

enum class SettingsGroupState {
    IDLE,
    EXPANDED,
    HALF_ALPHA
}

fun calculateGroupState(expandedGroup: MutableState<Int>, groupIndex: Int): SettingsGroupState {
    return when (expandedGroup.value) {
        -1 -> SettingsGroupState.IDLE
        groupIndex -> SettingsGroupState.EXPANDED
        else -> SettingsGroupState.HALF_ALPHA
    }
}

fun MutableState<Int>.onGroupHeaderClick(index: Int) {
    value = if (value == index) -1 else index
}
