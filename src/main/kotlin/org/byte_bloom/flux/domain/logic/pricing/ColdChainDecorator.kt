package org.byte_bloom.flux.domain.logic.pricing

private const val COLD_CHAIN_MULTIPLIER = 1.25

class ColdChainDecorator(
    packageComponent: PackageComponent
) : PackageDecorator(packageComponent) {

    override fun calculateTransitRate(baseRate: Double): Double {
        return super.calculateTransitRate(baseRate) *
                COLD_CHAIN_MULTIPLIER
    }
}
