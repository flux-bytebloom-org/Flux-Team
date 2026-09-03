package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Warehouse

class DecideRoutingWayUseCase(
    private val findFastestPathUseCase: FindFastestPathUseCase,
    private val findOptimalPathUseCase: FindOptimalPathUseCase
) {
    operator fun invoke(
        start: Warehouse,
        destination: Warehouse,
        isTripUrgent: Boolean
    ): List<Warehouse> {
        return if (isTripUrgent) {
            findFastestPathUseCase(start, destination)
        } else {
            findOptimalPathUseCase(start, destination)
        }
    }
}
