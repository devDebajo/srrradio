package ru.debajo.srrradio.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.debajo.srrradio.data.model.DbFavoriteStation
import ru.debajo.srrradio.data.model.DbStation

@Dao
internal interface DbFavoriteStationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DbFavoriteStation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<DbFavoriteStation>)

    @Query("DELETE FROM DbFavoriteStation WHERE stationId=:stationId")
    suspend fun delete(stationId: String)

    @Query("DELETE FROM DbFavoriteStation")
    suspend fun clear()

    @Query("SELECT * FROM DbStation JOIN DbFavoriteStation ON DbStation.id = DbFavoriteStation.stationId ORDER BY `order` ASC")
    fun observeStations(): Flow<List<DbStation>>

    @Query("SELECT * FROM DbStation JOIN DbFavoriteStation ON DbStation.id = DbFavoriteStation.stationId ORDER BY `order` ASC")
    suspend fun getStations(): List<DbStation>

    @Query("SELECT `order` FROM DbFavoriteStation ORDER BY `order` DESC LIMIT 1")
    suspend fun getMaxOrder(): Int?

    @Transaction
    suspend fun replace(items: List<DbFavoriteStation>) {
        clear()
        insert(items)
    }

    @Transaction
    suspend fun insertWithOrder(stationId: String) {
        val maxOrder = getMaxOrder() ?: -1
        insert(DbFavoriteStation(stationId, maxOrder + 1))
    }
}

// обновить ссылку на политику конфиденциальности
// пермишн на нотификации
// крестик закрывает приложение, но оно восстанавливается