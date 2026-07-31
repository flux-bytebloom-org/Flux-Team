package org.byte_bloom.flux.domain.pricing

class ExpressStrategy : DispatchStrategy {


    override fun calculateTransitCost(
        distanceKm: Double,
        weight: Double
    ): Double {

        return distanceKm * weight * 1.5
    }


    override fun getPriorityMultiplier(): Double {
        return 2.0
    }
}