package org.byte_bloom.flux.data.mapper

import org.byte_bloom.flux.data.dataholders.PackageRaw
import org.byte_bloom.flux.data.dataholders.RouteRaw
import org.byte_bloom.flux.data.dataholders.VehicleRaw
import org.byte_bloom.flux.data.dataholders.WarehouseRaw
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

fun PackageRaw.toDomain(): Package {
    return Package(
        id = this.id,
        weight = this.weight,
        originHub = Warehouse(id = this.originHubId, name = "", regionalZone = "", latitude = 0.0, longitude = 0.0),
        destinationHub = Warehouse(id = this.destinationHubId, name = "", regionalZone = "", latitude = 0.0, longitude = 0.0),
        priority = Priority.valueOf(this.priority.name)
    )
}

fun RouteRaw.toDomain(): Route {
    return Route(
        id = this.id,
        originHub = Warehouse(id = this.originHubId, name = "", regionalZone = "", latitude = 0.0, longitude = 0.0),
        destinationHub = Warehouse(id = this.destinationHubId, name = "", regionalZone = "", latitude = 0.0, longitude = 0.0),
        distanceKm = this.distanceKm,
        typicalDelayMin = this.typicalDelayMin
    )
}

fun VehicleRaw.toDomain(): Vehicle {
    return Vehicle(
        id = this.id,
        currentHub = Warehouse(id = this.currentHubId, name = "", regionalZone = "", latitude = 0.0, longitude = 0.0),
        maxCapacityKg = this.maxCapacityKg,
        costPerKm = this.costPerKm
    )
}

fun WarehouseRaw.toDomain(): Warehouse {
    return Warehouse(
        id = this.id,
        name = this.name,
        regionalZone = this.regionalZone,
        latitude = this.latitude,
        longitude = this.longitude
    )
}
