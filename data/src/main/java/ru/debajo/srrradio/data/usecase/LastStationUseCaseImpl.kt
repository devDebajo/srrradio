package ru.debajo.srrradio.data.usecase

import ru.debajo.srrradio.domain.LastStationUseCase

internal class LastStationUseCaseImpl(
    lastPlaylistIdPreference: LastPlaylistIdPreference,
    lastStationIdPreference: LastStationIdPreference,
) : LastStationUseCase {

    override var lastPlaylistId: String? by lastPlaylistIdPreference

    override var lastStationId: String? by lastStationIdPreference
}
