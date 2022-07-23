package ru.debajo.srrradio.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.debajo.srrradio.data.db.converter.SrrradioDbConverter
import ru.debajo.srrradio.data.db.dao.DbFavoriteStationDao
import ru.debajo.srrradio.data.db.dao.DbPlaylistDao
import ru.debajo.srrradio.data.db.dao.DbPlaylistMappingDao
import ru.debajo.srrradio.data.db.dao.DbStationDao
import ru.debajo.srrradio.data.db.dao.DbTrackCollectionItemDao
import ru.debajo.srrradio.data.model.DbFavoriteStation
import ru.debajo.srrradio.data.model.DbPlaylist
import ru.debajo.srrradio.data.model.DbPlaylistMapping
import ru.debajo.srrradio.data.model.DbStation
import ru.debajo.srrradio.data.model.DbTrackCollectionItem

@Database(
    entities = [
        DbPlaylist::class,
        DbStation::class,
        DbPlaylistMapping::class,
        DbFavoriteStation::class,
        DbTrackCollectionItem::class,
    ],
    version = 2,
)
@TypeConverters(SrrradioDbConverter::class)
internal abstract class SrrradioDatabase : RoomDatabase() {
    abstract fun dbPlaylistDao(): DbPlaylistDao
    abstract fun dbStationDao(): DbStationDao
    abstract fun dbPlaylistMappingDao(): DbPlaylistMappingDao
    abstract fun dbFavoriteStationDao(): DbFavoriteStationDao
    abstract fun dbTrackCollectionItemDao(): DbTrackCollectionItemDao
}
