package org.byte_bloom.flux.domain.algorithm.pricing

class EcoStrategy : DispatchStrategy {

    override fun calculateTransitCost(distanceKm: Double, weight: Double): Double {
        return distanceKm * weight * ECO_COST_PER_KM_PER_KG
    }

    override fun getPriorityMultiplier(): Double {
        return ECO_PRIORITY_MULTIPLIER
    }

    private companion object {
        const val ECO_COST_PER_KM_PER_KG = 0.5
        const val ECO_PRIORITY_MULTIPLIER = 1.0
    }
}

