package ru.debajo.srrradio.domain.model

data class Config(
    val databaseHomepage: String,
    val privacyPolicy: String,
    val defaultApiHost: String,
    val discoverApiHost: String,
    val authEnabled: Boolean,
    val snowFallEnabled: Boolean,
    val rateAppEnabled: Boolean,
    val lastVersionNumber: Int,
    val updateFileUrl: String?,
    val googlePlayInAppUpdateEnabled: Boolean,
)
