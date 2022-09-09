package ru.debajo.srrradio.di

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
inline fun <reified VM : ViewModel> diViewModel(key: String? = null): VM {
    return viewModel(
        key = key,
        factory = KoinViewModelFactory
    )
}

inline fun <reified VM : ViewModel> ComponentActivity.diViewModels(): Lazy<VM> {
    return viewModels(factoryProducer = { KoinViewModelFactory })
}
