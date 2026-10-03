package org.byte_bloom.flux.data.remote.mapper

import org.byte_bloom.flux.data.remote.dto.PackageRequestDto
import org.byte_bloom.flux.data.remote.dto.PackageResponseDto
import org.byte_bloom.flux.data.remote.dto.RouteRequestDto
import org.byte_bloom.flux.data.remote.dto.RouteResponseDto
import org.byte_bloom.flux.data.remote.dto.VehicleRequestDto
import org.byte_bloom.flux.data.remote.dto.VehicleResponseDto
import org.byte_bloom.flux.data.remote.dto.WarehouseRequestDto
import org.byte_bloom.flux.data.remote.dto.WarehouseResponseDto
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

private const val DEFAULT_COORDINATE = 0.0

private fun createEmptyWarehouse(id: String) = Warehouse(
    id = id,
    name = "",
    regionalZone = RegionalZone.UnKNOWN,
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

private fun parseWareHouseRegionalZone(value: String): RegionalZone = when(value.uppercase()) {
    "NORTH" -> RegionalZone.NORTH
    "SOUTH" -> RegionalZone.SOUTH
    "EAST" -> RegionalZone.EAST
    "WEST" -> RegionalZone.WEST
    "CENTRAL" -> RegionalZone.CENTRAL
    else -> RegionalZone.UnKNOWN
}

// ---------- Warehouse ----------

fun WarehouseResponseDto.toDomain(): Warehouse {
    return Warehouse(
        id = id,
        name = name,
        regionalZone = parseWareHouseRegionalZone(regionalZone),
        latitude = latitude,
        longitude = longitude
    )
}

fun Warehouse.toRequestDto(): WarehouseRequestDto {
    return WarehouseRequestDto(
        name = name,
        regionalZone = regionalZone.name,
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

fun VehicleResponseDto.toDomain(warehousesById: Map<String, Warehouse>): Vehicle? {
    val vehiclesHub = warehousesById[currentHubId]?:return null

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
