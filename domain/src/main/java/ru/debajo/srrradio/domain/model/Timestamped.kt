package ru.debajo.srrradio.domain.model

import org.joda.time.DateTime

data class Timestamped<T>(
    val value: T,
    val timestamp: DateTime,
) {
    operator fun compareTo(other: Timestamped<T>): Int {
        if (this === other) {
            return 0
        }

        return timestamp.compareTo(other.timestamp)
    }
}

fun <T> T.timestampedAt(timestamp: DateTime): Timestamped<T> {
    return Timestamped(this, timestamp)
}
