package org.byte_bloom.flux.domain.pricing

class EcoStrategy : DispatchStrategy {


    override fun calculateTransitCost(
        distanceKm: Double,
        weight: Double
    ): Double {

        return distanceKm * weight * 0.5
    }


    override fun getPriorityMultiplier(): Double {
        return 1.0
    }
}