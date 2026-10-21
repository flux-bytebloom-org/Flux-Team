package org.byte_bloom.flux.domain.algorithm.pricing.decorator


class ColdChainDecorator(
    packageComponent: PackageComponent
) : PackageDecorator(packageComponent) {

    override fun getDescription(): String {
        return "${packageComponent.getDescription()} + Cold Chain"
    }

    override fun calculateTransitRate(baseRate: Double): Double {
        return super.calculateTransitRate(baseRate) *
                COLD_CHAIN_MULTIPLIER
    }

    private companion object {
        const val COLD_CHAIN_MULTIPLIER = 1.25
    }
}

