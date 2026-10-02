package org.byte_bloom.flux.domain.testdata

import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

fun aWarehouse(
    id: String = "WH-001",
    name: String = "Test Hub",
    zone: RegionalZone = RegionalZone.NORTH
) = Warehouse(id, name, zone, 0.0, 0.0)

fun aVehicle(
    id: String = "V-001",
    hub: Warehouse = aWarehouse(),
    capacity: Double = 500.0,
    costPerKm: Double = 2.0
) = Vehicle(id, hub, capacity, costPerKm)
