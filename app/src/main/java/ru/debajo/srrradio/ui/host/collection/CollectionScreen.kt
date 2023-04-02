package ru.debajo.srrradio.ui.host.collection

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import ru.debajo.srrradio.R
import ru.debajo.srrradio.di.diViewModel
import ru.debajo.srrradio.ui.common.AppCard
import ru.debajo.srrradio.ui.common.AppScreenTitle
import ru.debajo.srrradio.ui.ext.longPress
import ru.debajo.srrradio.ui.host.main.LocalSnackbarLauncher

@Composable
fun <T> ListScreen(
    title: String,
    listBottomPadding: Dp = 0.dp,
    items: List<T>,
    key: (item: T) -> Any,
    onReorder: (from: Int, to: Int) -> Unit = { _, _ -> },
    onCommitReorder: (from: Int, to: Int) -> Unit = { _, _ -> },
    canReorder: Boolean = false,
    contentType: (item: T) -> Any = { "same_type" },
    emptyItemsContent: @Composable BoxScope.() -> Unit = {},
    itemContent: @Composable LazyItemScope.(item: T) -> Unit
) {
    val lazyColumnState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            AppScreenTitle(text = title)
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxSize()) {
                if (items.isEmpty()) {
                    emptyItemsContent()
                } else {
                    val state = rememberReorderableLazyListState(
                        listState = lazyColumnState,
                        onMove = { from, to ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onReorder(from.index, to.index)
                        },
                        onDragEnd = { from, to -> onCommitReorder(from, to) }
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .run {
                                if (canReorder) {
                                    reorderable(state).detectReorderAfterLongPress(state)
                                } else {
                                    this
                                }
                            },
                        state = lazyColumnState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = listBottomPadding)
                    ) {
                        items(
                            count = items.size,
                            key = { index -> key(items[index]) },
                            contentType = { index -> contentType(items[index]) },
                            itemContent = { index ->
                                val item = items[index]
                                ReorderableItem(state, key = key(item)) { isDragging ->
                                    val scale = animateFloatAsState(targetValue = if (isDragging) 1.05f else 1f)
                                    Box(modifier = Modifier.scale(scale.value)) {
                                        itemContent(item)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CollectionScreen() {
    val viewModel: CollectionViewModel = diViewModel()
    LaunchedEffect(viewModel) { viewModel.load() }

    val state by viewModel.state.collectAsState()
    val haptic = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarLauncher = LocalSnackbarLauncher.current

    ListScreen(
        title = stringResource(R.string.collection_title),
        items = state,
        key = { it.track },
        contentType = { "UiCollectionItem" },
        itemContent = { collectionItem ->
            CollectionItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement(),
                item = collectionItem,
                onDelete = { item ->
                    viewModel.remove(item)
                    haptic.longPress()
                },
                onClick = { item ->
                    snackbarLauncher.show(R.string.copied_to_clipboard)
                    clipboardManager.setText(AnnotatedString(item.track))
                    haptic.longPress()
                }
            )
        }
    )
}

@Composable
private fun CollectionItem(
    modifier: Modifier = Modifier,
    item: UiCollectionItem,
    onClick: (UiCollectionItem) -> Unit,
    onDelete: (UiCollectionItem) -> Unit
) {
    AppCard(
        modifier = modifier,
        onClick = { onClick(item) },
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = item.track,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.stationName,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { onDelete(item) }) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.accessibility_remove_track)
                    )
                }
            }
        }
    )
}
