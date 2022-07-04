package ru.debajo.srrradio.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.debajo.srrradio.data.model.DbPlaylistMapping

@Dao
internal interface DbPlaylistMappingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mapping: DbPlaylistMapping)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mappings: List<DbPlaylistMapping>)

    @Query("DELETE FROM DbPlaylistMapping WHERE playlistId=:playlistId")
    suspend fun deleteByPlaylistId(playlistId: String)
}
