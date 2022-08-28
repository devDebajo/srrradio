package ru.debajo.srrradio.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.debajo.srrradio.data.model.DbTrackCollectionItem

@Dao
internal interface DbTrackCollectionItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DbTrackCollectionItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(items: List<DbTrackCollectionItem>)

    @Query("DELETE FROM DbTrackCollectionItem WHERE name=:name")
    suspend fun delete(name: String)

    @Query("SELECT * FROM DbTrackCollectionItem")
    fun observe(): Flow<List<DbTrackCollectionItem>>

    @Query("SELECT * FROM DbTrackCollectionItem")
    suspend fun getAll(): List<DbTrackCollectionItem>
}
