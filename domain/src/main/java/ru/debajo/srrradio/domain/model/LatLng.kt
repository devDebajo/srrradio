package ru.debajo.srrradio.domain.model

data class LatLng(
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        fun from(latitude: Double?, longitude: Double?): LatLng? {
            latitude ?: return null
            longitude ?: return null
            return LatLng(latitude, longitude)
        }
    }
}
