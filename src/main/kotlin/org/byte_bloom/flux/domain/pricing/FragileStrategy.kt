package org.byte_bloom.flux.domain.pricing

class FragileStrategy : DispatchStrategy {


    override fun calculateTransitCost(
        distanceKm: Double,
        weight: Double
    ): Double {

        return (distanceKm * weight * 1.2) + 50
    }


    override fun getPriorityMultiplier(): Double {
        return 1.5
    }
}