package org.byte_bloom.flux.domain.operations.pricing

private const val ECO_COST_PER_KM_PER_KG = 0.5
private const val ECO_PRIORITY_MULTIPLIER = 1.0

class EcoStrategy : DispatchStrategy {


    override fun calculateTransitCost(
        distanceKm: Double,
        weight: Double
    ): Double {

        return distanceKm * weight * ECO_COST_PER_KM_PER_KG
    }


    override fun getPriorityMultiplier(): Double {
        return ECO_PRIORITY_MULTIPLIER
    }
}
