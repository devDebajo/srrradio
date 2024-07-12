package ru.debajo.srrradio.rate

enum class RateAppState(val code: Int) {
    RATED(0),
    NEVER(1),
    LATER(2),
    NOT_RATED(3);

    companion object {
        fun fromCode(code: Int): RateAppState {
            return RateAppState.entries.firstOrNull { it.code == code } ?: NOT_RATED
        }
    }
}
