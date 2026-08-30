package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.logic.pricing.RoutePricingEngine
import org.byte_bloom.flux.domain.model.Package

class CalculatePricingUseCase(
    private val pricingEngine: RoutePricingEngine
) {
    operator fun invoke(pkg: Package, distanceKm: Double): Double {
        val weight = pkg.weight ?: 0.0
        val baseCost = pricingEngine.calculateTransitCost(distanceKm, weight)
        return pkg.calculateTransitRate(baseCost)
    }
}

