package org.byte_bloom.flux.data.remote.dto

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

private const val DEFAULT_COORDINATE = 0.0

private fun createEmptyWarehouse(id: String) = Warehouse(
    id = id,
    name = "",
    regionalZone = "",
    latitude = DEFAULT_COORDINATE,
    longitude = DEFAULT_COORDINATE
)

private fun parsePackagePriority(value: String): Priority {
    return when (value.uppercase()) {
        "URGENT" -> Priority.URGENT
        "STANDARD" -> Priority.STANDARD
        else -> Priority.LOW
    }
}

// ---------- Warehouse ----------

fun WarehouseResponseDto.toDomain(): Warehouse {
    return Warehouse(
        id = id,
        name = name,
        regionalZone = regionalZone,
        latitude = latitude,
        longitude = longitude
    )
}

fun Warehouse.toRequestDto(): WarehouseRequestDto {
    return WarehouseRequestDto(
        name = name,
        regionalZone = regionalZone,
        latitude = latitude,
        longitude = longitude
    )
}

// ---------- Package ----------

// ---------- Package ----------

fun PackageResponseDto.toDomain(warehousesById: Map<String, Warehouse>): Package? {
    val origin = warehousesById[originHubId]
    val destination = warehousesById[destinationHubId]
    if (origin == null || destination == null) return null

    return Package(
        id = id,
        weight = weight,
        originHub = origin,
        destinationHub = destination,
        priority = parsePackagePriority(priority)
    )
}

fun Package.toRequestDto(): PackageRequestDto {
    return PackageRequestDto(
        weight = weight,
        originHubId = originHub.id,
        destinationHubId = destinationHub.id,
        priority = priority.name
    )
}

// ---------- Vehicle ----------

fun VehicleResponseDto.toDomain(warehousesById: Map<String, Warehouse>): Vehicle {
    val vehiclesHub = warehousesById[currentHubId]
        ?:throw LogisticsException.EntityNotFoundException.WarehouseNotFoundException(currentHubId)

    return Vehicle(
        id = id,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm,
        currentHub = vehiclesHub
    )
}

fun Vehicle.toRequestDto(): VehicleRequestDto {
    return VehicleRequestDto(
        currentHubId = currentHub.id,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm
    )
}

// ---------- Route ----------

fun RouteResponseDto.toDomain(warehousesById: Map<String, Warehouse>): Route? {
    val origin = warehousesById[originHubId]
    val destination = warehousesById[destinationHubId]
    if (origin == null || destination == null) return null

    return Route(
        id = id,
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin,
        originHub = origin,
        destinationHub = destination
    )
}

fun Route.toRequestDto(): RouteRequestDto {
    return RouteRequestDto(
        originHubId = originHub.id,
        destinationHubId = destinationHub.id,
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin
    )
}
