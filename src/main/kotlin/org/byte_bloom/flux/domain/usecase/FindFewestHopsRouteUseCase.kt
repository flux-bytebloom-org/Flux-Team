package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.logic.routing.BreadthFirstRouter
import org.byte_bloom.flux.domain.logic.routing.RoutingSearchResult
import org.byte_bloom.flux.domain.model.Warehouse

class FindFewestHopsRouteUseCase(private val breadthFirstRouter: BreadthFirstRouter) {
    operator fun invoke(start: Warehouse, destination: Warehouse): RoutingSearchResult {
        return breadthFirstRouter.findLeastHopPath(start, destination)
    }
}
