package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.builder.ShadowWarehouseGraphBuilder
import org.byte_bloom.flux.domain.model.Warehouse

class PenalizeBottleneckRoutesUseCase(
    private val shadowWarehouseGraphBuilder: ShadowWarehouseGraphBuilder
) {

    operator fun invoke(
        bottleneckWarehouse: Warehouse,
        allWarehouses: List<Warehouse>,
        penaltyFactor: Double = 1.3
    ): Map<String, Warehouse> {

        return shadowWarehouseGraphBuilder.build(
            allWarehouses = allWarehouses,
            bottleneckWarehouse = bottleneckWarehouse,
            penaltyFactor = penaltyFactor
        )
    }
}
