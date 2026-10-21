package org.byte_bloom.flux.domain.algorithm.pricing

class FragileStrategy : DispatchStrategy {
    override fun calculateTransitCost(distanceKm: Double, weight: Double): Double {
        return (distanceKm * weight * FRAGILE_COST_MULTIPLIER) + FRAGILE_BASE_COST
    }

    override fun getPriorityMultiplier(): Double {
        return FRAGILE_PRIORITY_MULTIPLIER
    }

    private companion object {
        const val FRAGILE_COST_MULTIPLIER = 1.2
        const val FRAGILE_PRIORITY_MULTIPLIER = 1.5
        const val FRAGILE_BASE_COST = 50
    }
}

