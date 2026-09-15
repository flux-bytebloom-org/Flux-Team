package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.builder.ShadowWarehouseGraphBuilder
import org.byte_bloom.flux.domain.model.Warehouse

private const val DEFAULT_BOTTLENECK_PENALTY_FACTOR = 1.3

class PenalizeBottleneckRoutesUseCase(
    private val shadowWarehouseGraphBuilder: ShadowWarehouseGraphBuilder
) {

    operator fun invoke(
        bottleneckWarehouse: Warehouse,
        allWarehouses: List<Warehouse>,
        penaltyFactor: Double = DEFAULT_BOTTLENECK_PENALTY_FACTOR
    ): Map<String, Warehouse> {

        return shadowWarehouseGraphBuilder.build(
            allWarehouses = allWarehouses,
            bottleneckWarehouse = bottleneckWarehouse,
            penaltyFactor = penaltyFactor
        )
    }
}
