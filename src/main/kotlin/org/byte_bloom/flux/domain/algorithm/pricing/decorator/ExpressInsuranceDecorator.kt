package org.byte_bloom.flux.domain.algorithm.pricing.decorator

class ExpressInsuranceDecorator(
    packageComponent: PackageComponent
) : PackageDecorator(packageComponent) {

    override fun getDescription(): String {
        return "${packageComponent.getDescription()} + Express Insurance"
    }

    override fun calculateTransitRate(baseRate: Double): Double {
        return super.calculateTransitRate(baseRate) +
                EXPRESS_INSURANCE_PREMIUM
    }
    private companion object {
        const val EXPRESS_INSURANCE_PREMIUM = 25.0
    }
}

