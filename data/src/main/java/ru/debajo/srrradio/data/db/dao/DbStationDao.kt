package ru.debajo.srrradio.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.debajo.srrradio.data.model.DbStation

@Dao
internal interface DbStationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(station: DbStation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stations: List<DbStation>)

    @Query("SELECT * FROM dbstation WHERE id in (SELECT stationId FROM DbPlaylistMapping WHERE playlistId=:playlistId)")
    suspend fun getStationsByPlaylist(playlistId: String): List<DbStation>
}
