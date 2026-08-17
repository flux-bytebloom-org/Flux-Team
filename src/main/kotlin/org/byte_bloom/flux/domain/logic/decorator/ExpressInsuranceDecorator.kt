package org.byte_bloom.flux.domain.logic.decorator

import org.byte_bloom.flux.domain.model.PackageComponent

private const val EXPRESS_INSURANCE_PREMIUM = 25.0

class ExpressInsuranceDecorator(
    packageComponent: PackageComponent
) : PackageDecorator(packageComponent) {

    override fun calculateTransitRate(baseRate: Double): Double {
        return super.calculateTransitRate(baseRate) +
                EXPRESS_INSURANCE_PREMIUM
    }
}
