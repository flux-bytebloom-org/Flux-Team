package org.byte_bloom.flux.data.csv.mapper

import org.byte_bloom.flux.data.csv.dataholders.RouteRaw
import org.byte_bloom.flux.data.csv.dataholders.VehicleRaw
import org.byte_bloom.flux.data.csv.dataholders.Priority
import org.byte_bloom.flux.data.csv.dataholders.PackageRaw
import org.byte_bloom.flux.data.csv.dataholders.WarehouseRaw
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.model.Priority as DomainPriority

fun WarehouseRaw.toDomain() = Warehouse(
    id = id, name = name, regionalZone = regionalZone,
    latitude = latitude, longitude = longitude
)

fun VehicleRaw.toDomain(warehousesById: Map<String, Warehouse>): Vehicle? {
    val currentHub = warehousesById[currentHubId] ?: return null
    return Vehicle(id = id, maxCapacityKg = maxCapacityKg, costPerKm = costPerKm, currentHub = currentHub)
}

fun PackageRaw.toDomain(warehousesById: Map<String, Warehouse>): Package? {
    val origin = warehousesById[originHubId]
    val destination = warehousesById[destinationHubId]
    if (origin == null || destination == null) return null

    return Package(id = id,weight = weight,originHub = origin,
        destinationHub = destination, priority = priority.toDomain()
    )
}

fun RouteRaw.toDomain(warehousesById: Map<String, Warehouse>): Route? {
    val origin = warehousesById[originHubId]
    val destination = warehousesById[destinationHubId]
    if (origin == null || destination == null) return null

    return Route(id = id, distanceKm = distanceKm, typicalDelayMin = typicalDelayMin,
        originHub = origin,destinationHub = destination)
}

fun Priority.toDomain(): DomainPriority = when (this) {
    Priority.LOW -> DomainPriority.LOW
    Priority.STANDARD -> DomainPriority.STANDARD
    Priority.URGENT -> DomainPriority.URGENT
}
