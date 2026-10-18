package org.byte_bloom.flux.domain.mapper

import org.byte_bloom.flux.domain.model.CargoQuery
import org.byte_bloom.flux.domain.model.CargoRecord
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse

fun Package.toCargoQuery(): CargoQuery? {
    val weightKg = weight?.takeIf { it.isFinite() && it > 0.0 } ?: return null
    return CargoQuery(
        latitude = destinationHub.latitude,
        longitude = destinationHub.longitude,
        weightKg = weightKg
    )
}

fun Package.toCargoRecord(): CargoRecord? {
    val query = toCargoQuery() ?: return null
    val zone = destinationHub.regionalZone
    if (zone == RegionalZone.UnKNOWN || !destinationHub.hasKnownLocation()) return null

    return CargoRecord(
        latitude = query.latitude,
        longitude = query.longitude,
        weightKg = query.weightKg,
        sector = zone
    )
}

fun List<Package>.toCargoRecords(excludingId: String? = null): List<CargoRecord> =
    filter { it.id != excludingId }.mapNotNull { it.toCargoRecord() }

// The warehouse parser falls back to 0.0, 0.0 when coordinates are invalid
private fun Warehouse.hasKnownLocation(): Boolean = !(latitude == 0.0 && longitude == 0.0)
