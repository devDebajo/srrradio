package ru.debajo.srrradio.ui.host.collection

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.debajo.srrradio.R
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.ui.common.AppCard
import ru.debajo.srrradio.ui.ext.longPress
import ru.debajo.srrradio.ui.host.main.LocalSnackbarLauncher

@Composable
fun <T> ListScreen(
    title: String,
    listBottomPadding: Dp = 0.dp,
    items: List<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    emptyItemsContent: @Composable BoxScope.() -> Unit = {},
    itemContent: @Composable LazyItemScope.(item: T) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                lineHeight = 44.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxSize()) {
                if (items.isEmpty()) {
                    emptyItemsContent()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = listBottomPadding)
                    ) {
                        items(
                            items = items,
                            key = key,
                            contentType = contentType,
                            itemContent = itemContent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CollectionScreen() {
    val viewModel = viewModel { AppApiHolder.get().collectionViewModel }
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
