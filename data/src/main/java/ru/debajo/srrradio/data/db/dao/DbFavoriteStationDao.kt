package ru.debajo.srrradio.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.debajo.srrradio.data.model.DbFavoriteStation
import ru.debajo.srrradio.data.model.DbStation

@Dao
internal interface DbFavoriteStationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DbFavoriteStation)

    @Query("DELETE FROM DbFavoriteStation WHERE stationId=:stationId")
    suspend fun delete(stationId: String)

    @Query("SELECT * FROM DbStation WHERE id in (SELECT stationId FROM DbFavoriteStation)")
    fun observeStations(): Flow<List<DbStation>>

    @Query("SELECT * FROM DbStation WHERE id in (SELECT stationId FROM DbFavoriteStation)")
    suspend fun getStations(): List<DbStation>
}
