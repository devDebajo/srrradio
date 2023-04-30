package ru.debajo.srrradio.ui.host.main.list.model

sealed interface StationsListNews {
    class ShowToast(val stringRes: Int) : StationsListNews
    object ScrollToTop : StationsListNews
}
