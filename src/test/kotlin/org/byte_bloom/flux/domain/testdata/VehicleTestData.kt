package org.byte_bloom.flux.domain.testdata

import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.model.Package

fun aWarehouse(
    id: String = "WH-001",
    name: String = "Test Hub",
    zone: RegionalZone = RegionalZone.NORTH
) = Warehouse(id, name, zone, 0.0, 0.0)


fun aPackage(
    id: String = "PKG-001",
    weight: Double? = 10.0,
    origin: Warehouse = aWarehouse(id = "WH-001"),
    destination: Warehouse = aWarehouse(id = "WH-002"),
    priority: Priority = Priority.STANDARD
) = Package(id, weight, origin, destination, priority)

fun aVehicle(
    id: String = "V-1",
    hub: Warehouse = createTestWarehouse(),
    capacity: Double = 100.0,
    costPerKm: Double = 1.0
) = Vehicle(id, hub, capacity, costPerKm)
