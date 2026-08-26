package org.byte_bloom.flux.domain.logic.pricing

private const val EXPRESS_COST_MULTIPLIER = 1.5
private const val EXPRESS_PRIORITY_MULTIPLIER = 2.0

class ExpressStrategy : DispatchStrategy {


    override fun calculateTransitCost(
        distanceKm: Double,
        weight: Double
    ): Double {

        return distanceKm * weight * EXPRESS_COST_MULTIPLIER
    }


    override fun getPriorityMultiplier(): Double {
        return EXPRESS_PRIORITY_MULTIPLIER
    }
}
