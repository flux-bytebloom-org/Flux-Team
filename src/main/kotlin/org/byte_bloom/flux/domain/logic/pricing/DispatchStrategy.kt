package org.byte_bloom.flux.domain.logic.pricing

interface DispatchStrategy {

    fun calculateTransitCost(
        distanceKm: Double,
        weight: Double
    ): Double


    fun getPriorityMultiplier(): Double
}

