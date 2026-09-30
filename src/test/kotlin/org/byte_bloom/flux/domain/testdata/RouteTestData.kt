package org.byte_bloom.flux.domain.testdata

import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Warehouse

fun createRouteTestWarehouse(id: String = "WH-1") =
    Warehouse(id, "Test Warehouse $id", RegionalZone.CENTRAL, 0.0, 0.0)

fun createTestRoute(
    id: String = "RT-1",
    origin: Warehouse = createRouteTestWarehouse("WH-1"),
    destination: Warehouse = createRouteTestWarehouse("WH-2"),
    distanceKm: Double = 100.0,
    typicalDelayMin: Double = 10.0
) = Route(id, origin, destination, distanceKm, typicalDelayMin)
