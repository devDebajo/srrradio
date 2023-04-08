package ru.debajo.srrradio.data.appversion

abstract class AppVersionMigration(val targetVersion: Int) {
    abstract val allowFail: Boolean

    abstract suspend fun migrate()
}
