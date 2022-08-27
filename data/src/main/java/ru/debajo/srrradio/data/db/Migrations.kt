package ru.debajo.srrradio.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATIONS: Array<Migration> = arrayOf(
    object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `DbTrackCollectionItem` (`name` TEXT NOT NULL, `stationId` TEXT NOT NULL, `stationName` TEXT NOT NULL, PRIMARY KEY(`name`))")
        }
    },

    object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `DbStation` ADD COLUMN `latitude` REAL DEFAULT null")
            database.execSQL("ALTER TABLE `DbStation` ADD COLUMN `longitude` REAL DEFAULT null")
        }
    },
)
