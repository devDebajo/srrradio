package ru.debajo.srrradio.domain.model

data class Config(
    val authEnabled: Boolean = false,
    val snowFallEnabled: Boolean = false,
    val rateAppEnabled: Boolean = false,
)