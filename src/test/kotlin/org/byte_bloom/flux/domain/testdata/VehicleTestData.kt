package org.byte_bloom.flux.domain.testdata

import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

fun createTestVehicle(
    id: String = "V-1",
    hub: Warehouse = createTestWarehouse(),
    maxCapacityKg: Double = 100.0,
    costPerKm: Double = 1.0
) = Vehicle(id, hub, maxCapacityKg, costPerKm)