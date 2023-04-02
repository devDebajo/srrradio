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

    @Query("SELECT * FROM dbstation WHERE stream=:url")
    suspend fun getByUrl(url: String): DbStation?

    @Query("SELECT * FROM dbstation WHERE latitude IS NOT NULL AND longitude IS NOT NULL AND alive=true")
    suspend fun getAllWithLocation(): List<DbStation>
}
