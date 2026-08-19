package org.byte_bloom.flux.data.mapper

import org.byte_bloom.flux.data.dataholders.PackageRaw
import org.byte_bloom.flux.data.dataholders.Priority
import org.byte_bloom.flux.data.dataholders.RouteRaw
import org.byte_bloom.flux.data.dataholders.VehicleRaw
import org.byte_bloom.flux.data.dataholders.WarehouseRaw
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.model.Priority as DomainPriority

fun WarehouseRaw.toDomain() = Warehouse(
    id = id,
    name = name,
    regionalZone = regionalZone,
    latitude = latitude,
    longitude = longitude
)

fun VehicleRaw.toDomain() = Vehicle(
    id = id,
    maxCapacityKg = maxCapacityKg,
    costPerKm = costPerKm,
    currentHub = Warehouse(
        id = currentHubId,
        name = "",
        regionalZone = "",
        latitude = 0.0,
        longitude = 0.0
    )
)

fun PackageRaw.toDomain(
    origin: Warehouse,
    destination: Warehouse
) = Package(
    id = id,
    weight = weight,
    originHub = origin,
    destinationHub = destination,
    priority = priority.toDomain()
)

fun RouteRaw.toDomain(
    origin: Warehouse,
    destination: Warehouse
) = Route(
    id = id,
    distanceKm = distanceKm,
    typicalDelayMin = typicalDelayMin,
    originHub = origin,
    destinationHub = destination
)

fun Priority.toDomain(): DomainPriority = when (this) {
    Priority.LOW -> DomainPriority.LOW
    Priority.STANDARD -> DomainPriority.STANDARD
    Priority.URGENT -> DomainPriority.URGENT
}
