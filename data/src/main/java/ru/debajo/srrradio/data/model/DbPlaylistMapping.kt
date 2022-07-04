package ru.debajo.srrradio.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["playlistId", "stationId"])
internal data class DbPlaylistMapping(
    @ColumnInfo(name = "playlistId")
    val playlistId: String,

    @ColumnInfo(name = "stationId")
    val stationId: String,
)
