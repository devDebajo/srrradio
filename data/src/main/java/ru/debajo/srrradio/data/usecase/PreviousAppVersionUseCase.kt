package ru.debajo.srrradio.data.usecase

import java.util.concurrent.CancellationException
import ru.debajo.srrradio.common.AppVersion
import ru.debajo.srrradio.data.appversion.AppVersionMigrationsList
import ru.debajo.srrradio.data.preference.PreviousAppVersionPreference
import timber.log.Timber

class AppVersionMigrateUseCase(
    private val currentAppVersion: AppVersion,
    private val appVersionMigrationsList: AppVersionMigrationsList,
    private val previousAppVersionPreference: PreviousAppVersionPreference,
) {
    suspend fun migrate() {
        val previousNumber = previousAppVersionPreference.get()
        if (previousNumber.number == currentAppVersion.number) {
            Timber.tag("AppVersionMigrateUseCase").d("Skip app migration")
            return
        }

        val migrations = appVersionMigrationsList.getList().associateBy { it.targetVersion }
        for (number in (previousNumber.number + 1)..currentAppVersion.number) {
            val migration = migrations[number] ?: continue
            Timber.tag("AppVersionMigrateUseCase").d("Start migration to $number")
            try {
                migration.migrate()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                Timber.tag("AppVersionMigrateUseCase").e(e, "Migrate error, number: $number")
                if (!migration.allowFail) {
                    throw e
                }
            }
            Timber.tag("AppVersionMigrateUseCase").d("Success migration to $number")
        }
        previousAppVersionPreference.set(currentAppVersion)
    }
}
