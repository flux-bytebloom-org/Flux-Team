package org.byte_bloom.flux.domain.logic.decorator

import org.byte_bloom.flux.domain.model.PackageComponent

private const val FRAGILE_PROTECTIVE_FEE = 50.0

class FragileHandlingDecorator(
    packageComponent: PackageComponent
) : PackageDecorator(packageComponent) {

    override fun calculateTransitRate(baseRate: Double): Double {
        return super.calculateTransitRate(baseRate) +
                FRAGILE_PROTECTIVE_FEE
    }
}
