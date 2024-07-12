package ru.debajo.srrradio.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATIONS: Array<Migration> = arrayOf(
    object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `DbTrackCollectionItem` (`name` TEXT NOT NULL, `stationId` TEXT NOT NULL, `stationName` TEXT NOT NULL, PRIMARY KEY(`name`))")
        }
    },

    object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `DbStation` ADD COLUMN `latitude` REAL DEFAULT null")
            db.execSQL("ALTER TABLE `DbStation` ADD COLUMN `longitude` REAL DEFAULT null")
        }
    },

    object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `DbFavoriteStation` ADD COLUMN `order` INTEGER NOT NULL DEFAULT -1")
        }
    },

    object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `DbStation` ADD COLUMN `alive` INTEGER NOT NULL DEFAULT 1")
        }
    },

    object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `DbStation` ADD COLUMN `tags` TEXT NOT NULL DEFAULT ''")
        }
    },
)
