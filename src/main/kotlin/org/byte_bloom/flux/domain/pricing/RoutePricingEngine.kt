package org.byte_bloom.flux.domain.pricing


class RoutePricingEngine(
    private var strategy: DispatchStrategy
) {


    fun calculateTransitCost(
        distanceKm: Double,
        weight: Double
    ): Double {

        return strategy.calculateTransitCost(
            distanceKm,
            weight
        )
    }



    fun changeStrategy(
        newStrategy: DispatchStrategy
    ) {

        strategy = newStrategy
    }
}