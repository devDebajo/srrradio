package ru.debajo.srrradio.common

data class AppVersion(
    val versionName: String,
    val number: Int,
) {
    operator fun compareTo(other: AppVersion): Int {
        return number.compareTo(other.number)
    }
}
