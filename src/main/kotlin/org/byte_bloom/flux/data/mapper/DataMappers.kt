package org.byte_bloom.flux.data.mapper

import org.byte_bloom.flux.data.dataholders.RouteRaw
import org.byte_bloom.flux.data.dataholders.VehicleRaw
import org.byte_bloom.flux.data.dataholders.Priority
import org.byte_bloom.flux.data.dataholders.PackageRaw
import org.byte_bloom.flux.data.dataholders.WarehouseRaw
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.model.Priority as DomainPriority

private const val DEFAULT_COORDINATE = 0.0

private fun createEmptyWarehouse(id: String) = Warehouse(
    id = id, name = "", regionalZone = "",
    latitude = DEFAULT_COORDINATE, longitude = DEFAULT_COORDINATE
)

fun WarehouseRaw.toDomain() = Warehouse(
    id = id, name = name, regionalZone = regionalZone,
    latitude = latitude, longitude = longitude
)

fun VehicleRaw.toDomain() = Vehicle(
    id = id, maxCapacityKg = maxCapacityKg, costPerKm = costPerKm,
    currentHub = createEmptyWarehouse(currentHubId)
)

fun PackageRaw.toDomain() = Package(
    id = id, weight = weight,
    originHub = createEmptyWarehouse(originHubId),
    destinationHub = createEmptyWarehouse(destinationHubId),
    priority = priority.toDomain()
)

fun RouteRaw.toDomain() = Route(
    id = id, distanceKm = distanceKm, typicalDelayMin = typicalDelayMin,
    originHub = createEmptyWarehouse(originHubId),
    destinationHub = createEmptyWarehouse(destinationHubId)
)

fun Priority.toDomain(): DomainPriority = when (this) {
    Priority.LOW -> DomainPriority.LOW
    Priority.STANDARD -> DomainPriority.STANDARD
    Priority.URGENT -> DomainPriority.URGENT
}

