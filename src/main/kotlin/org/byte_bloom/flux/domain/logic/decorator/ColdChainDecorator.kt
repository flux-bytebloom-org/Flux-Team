package org.byte_bloom.flux.domain.logic.decorator

import org.byte_bloom.flux.domain.model.PackageComponent

private const val COLD_CHAIN_MULTIPLIER = 1.25

class ColdChainDecorator(
    packageComponent: PackageComponent
) : PackageDecorator(packageComponent) {

    override fun calculateTransitRate(baseRate: Double): Double {
        return super.calculateTransitRate(baseRate) *
                COLD_CHAIN_MULTIPLIER
    }
}
