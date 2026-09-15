package org.byte_bloom.flux.data.remote.dto

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

private const val DEFAULT_COORDINATE = 0.0

private fun createEmptyWarehouse(id: String) = Warehouse(
    id = id, name = "", regionalZone = "",
    latitude = DEFAULT_COORDINATE, longitude = DEFAULT_COORDINATE
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
        regionalZone = regional_zone,
        latitude = latitude,
        longitude = longitude
    )
}

fun Warehouse.toRequestDto(): WarehouseRequestDto {
    return WarehouseRequestDto(
        name = name,
        regional_zone = regionalZone,
        latitude = latitude,
        longitude = longitude
    )
}

// ---------- Package ----------

fun PackageResponseDto.toDomain(): Package {
    return Package(
        id = id,
        weight = weight,
        originHub = createEmptyWarehouse(origin_hub_id),
        destinationHub = createEmptyWarehouse(destination_hub_id),
        priority = parsePackagePriority(priority)
    )
}

fun Package.toRequestDto(): PackageRequestDto {
    return PackageRequestDto(
        weight = weight,
        origin_hub_id = originHub.id,
        destination_hub_id = destinationHub.id,
        priority = priority.name
    )
}

// ---------- Vehicle ----------

fun VehicleResponseDto.toDomain(): Vehicle {
    return Vehicle(
        id = id,
        maxCapacityKg = max_capacity_kg,
        costPerKm = cost_per_km,
        currentHub = createEmptyWarehouse(current_hub_id)
    )
}

fun Vehicle.toRequestDto(): VehicleRequestDto {
    return VehicleRequestDto(
        current_hub_id = currentHub.id,
        max_capacity_kg = maxCapacityKg,
        cost_per_km = costPerKm
    )
}

// ---------- Route ----------

fun RouteResponseDto.toDomain(): Route {
    return Route(
        id = id,
        distanceKm = distance_km,
        typicalDelayMin = typical_delay_min,
        originHub = createEmptyWarehouse(origin_hub_id),
        destinationHub = createEmptyWarehouse(destination_hub_id)
    )
}

fun Route.toRequestDto(): RouteRequestDto {
    return RouteRequestDto(
        origin_hub_id = originHub.id,
        destination_hub_id = destinationHub.id,
        distance_km = distanceKm,
        typical_delay_min = typicalDelayMin
    )
}
