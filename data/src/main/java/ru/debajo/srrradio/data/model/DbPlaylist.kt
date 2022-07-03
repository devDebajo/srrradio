package ru.debajo.srrradio.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.debajo.srrradio.domain.model.Playlist

@Entity
internal data class DbPlaylist(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,
)

internal fun Playlist.toDb(): DbPlaylist {
    return DbPlaylist(id = id, name = name)
}
