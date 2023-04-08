package ru.debajo.srrradio.data.appversion

class AppVersionMigrationsList(
    private val appVersionMigrationTo10: AppVersionMigrationTo10
) {
    fun getList(): List<AppVersionMigration> {
        return listOf(appVersionMigrationTo10)
    }
}
