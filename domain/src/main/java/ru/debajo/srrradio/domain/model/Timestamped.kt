package ru.debajo.srrradio.domain.model

import org.joda.time.DateTime

data class Timestamped<T>(
    val value: T,
    val timestamp: DateTime,
)

fun <T> T.timestampedAt(timestamp: DateTime): Timestamped<T> {
    return Timestamped(this, timestamp)
}
