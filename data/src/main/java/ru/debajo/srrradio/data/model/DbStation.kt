package ru.debajo.srrradio.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.debajo.srrradio.domain.model.Station

@Entity
internal data class DbStation(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "stream")
    val stream: String,

    @ColumnInfo(name = "image")
    val image: String?
)

internal fun Station.toDb(): DbStation {
    return DbStation(
        id = id,
        name = name,
        stream = stream,
        image = image,
    )
}

internal fun DbStation.toDomain(): Station {
    return Station(
        id = id,
        name = name,
        stream = stream,
        image = image,
    )
}
