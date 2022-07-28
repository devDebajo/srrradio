package ru.debajo.srrradio.ui.host.main.settings.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.debajo.srrradio.error.SendErrorsHelper

class LogsListViewModel(
    private val sendErrorsHelper: SendErrorsHelper
) : ViewModel() {

    private val stateMutable: MutableStateFlow<List<LogsListItem>> = MutableStateFlow(emptyList())
    private val newsMutable: MutableSharedFlow<LogsScreenNews> = MutableSharedFlow()
    val state: StateFlow<List<LogsListItem>> = stateMutable.asStateFlow()
    val news: SharedFlow<LogsScreenNews> = newsMutable.asSharedFlow()

    fun load() {
        viewModelScope.launch {
            stateMutable.value = sendErrorsHelper.getFiles().mapNotNull {
                val date = sendErrorsHelper.getDate(it)
                if (date != null) {
                    LogsListItem(
                        path = it.absolutePath,
                        title = date.toString("dd MMM yyyy")
                    )
                } else {
                    null
                }
            }
        }
    }

    fun onFileClick(logItem: LogsListItem) {
        viewModelScope.launch {
            newsMutable.emit(LogsScreenNews.OpenMailApp(logItem.path))
        }
    }
}
