package ru.debajo.srrradio.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class DbTrackCollectionItem(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "stationId")
    val stationId: String,

    @ColumnInfo(name = "stationName")
    val stationName: String,
)
