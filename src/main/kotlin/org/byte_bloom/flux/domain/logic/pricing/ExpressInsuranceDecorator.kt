package org.byte_bloom.flux.domain.logic.pricing

private const val EXPRESS_INSURANCE_PREMIUM = 25.0

class ExpressInsuranceDecorator(
    packageComponent: PackageComponent
) : PackageDecorator(packageComponent) {

    override fun calculateTransitRate(baseRate: Double): Double {
        return super.calculateTransitRate(baseRate) +
                EXPRESS_INSURANCE_PREMIUM
    }
}
