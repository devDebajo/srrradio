package ru.debajo.srrradio.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class DbFavoriteStation(
    @PrimaryKey
    @ColumnInfo("stationId")
    val stationId: String,

    @ColumnInfo("order")
    val order: Int,
)
