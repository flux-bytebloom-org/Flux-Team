package org.byte_bloom.flux.domain.operations.pricing

private const val FRAGILE_COST_MULTIPLIER = 1.2
private const val FRAGILE_PRIORITY_MULTIPLIER = 1.5
private const val FRAGILE_BASE_COST = 50
class FragileStrategy : DispatchStrategy {


    override fun calculateTransitCost(
        distanceKm: Double,
        weight: Double
    ): Double {

        return (distanceKm * weight * FRAGILE_COST_MULTIPLIER) + FRAGILE_BASE_COST
    }


    override fun getPriorityMultiplier(): Double {
        return FRAGILE_PRIORITY_MULTIPLIER
    }
}
