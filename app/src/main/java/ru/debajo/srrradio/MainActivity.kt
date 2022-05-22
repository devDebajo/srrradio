package ru.debajo.srrradio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import ru.debajo.srrradio.common.presentation.viewModelsBy
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.ui.list.StationsList
import ru.debajo.srrradio.ui.list.StationsListViewModel
import ru.debajo.srrradio.ui.theme.SrrradioTheme

class MainActivity : ComponentActivity() {

    private val stationsListViewModel: StationsListViewModel by viewModelsBy { AppApiHolder.get().stationsListViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(
                StationsListViewModel.Local provides stationsListViewModel,
            ) {
                SrrradioTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    StationsList()
}
