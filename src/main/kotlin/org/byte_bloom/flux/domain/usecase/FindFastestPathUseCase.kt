package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.algorithm.routing.DijkstraRouter
import org.byte_bloom.flux.domain.algorithm.routing.RoutingCriterion
import org.byte_bloom.flux.domain.model.Warehouse

class FindFastestPathUseCase(private val dijkstraRouter: DijkstraRouter) {

    operator fun invoke(start: Warehouse, destination: Warehouse): List<Warehouse> {
        return dijkstraRouter.findShortestPath(start, destination, RoutingCriterion.TIME_DELAY)
    }
}
