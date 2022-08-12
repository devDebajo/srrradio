package ru.debajo.srrradio.icon

enum class AppIcon(val componentSegment: String) {
    DYNAMIC("dynamic"),
    NEON("neon"),
    WAVE("wave"),
    MINT("mint");

    companion object {
        val DEFAULT: AppIcon = NEON
    }
}
