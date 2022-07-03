package ru.debajo.srrradio.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.debajo.srrradio.data.model.DbPlaylist

@Dao
internal interface DbPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlist: DbPlaylist)

    @Query("DELETE FROM dbplaylist WHERE id=:playlistId")
    suspend fun delete(playlistId: String)

    @Query("SELECT * FROM dbplaylist WHERE id=:id")
    suspend fun getPlaylist(id: String): DbPlaylist?
}
