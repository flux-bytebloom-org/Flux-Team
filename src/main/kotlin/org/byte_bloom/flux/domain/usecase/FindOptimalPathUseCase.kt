package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.logic.routing.DijkstraRouter
import org.byte_bloom.flux.domain.model.Warehouse

class FindOptimalPathUseCase(private val dijkstraRouter: DijkstraRouter){
    operator fun invoke(start: Warehouse, destination: Warehouse): List<Warehouse> {

        return dijkstraRouter.findShortestPath(start, destination)
    }
}


