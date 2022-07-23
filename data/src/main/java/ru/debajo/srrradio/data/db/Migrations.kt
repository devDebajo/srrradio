package ru.debajo.srrradio.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATIONS: Array<Migration> = arrayOf(
    object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `DbTrackCollectionItem` (`name` TEXT NOT NULL, `stationId` TEXT NOT NULL, `stationName` TEXT NOT NULL, PRIMARY KEY(`name`))")
        }
    },
)
