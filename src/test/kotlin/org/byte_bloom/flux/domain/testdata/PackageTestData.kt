package org.byte_bloom.flux.domain.testdata

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse

fun createTestWarehouse(id: String = "WH-1") =
    Warehouse(id, "Test Warehouse", RegionalZone.CENTRAL, 0.0, 0.0)

fun createTestPackage(
    id: String = "PKG-1",
    weight: Double? = 10.0,
    origin: Warehouse = createTestWarehouse("WH-1"),
    destination: Warehouse = createTestWarehouse("WH-2"),
    priority: Priority = Priority.STANDARD
) = Package(id, weight, origin, destination, priority)