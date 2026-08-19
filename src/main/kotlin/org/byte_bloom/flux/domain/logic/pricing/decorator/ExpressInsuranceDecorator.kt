<<<<<<<< HEAD:src/main/kotlin/org/byte_bloom/flux/domain/logic/decorator/ExpressInsuranceDecorator.kt
package org.byte_bloom.flux.domain.logic.decorator

import org.byte_bloom.flux.domain.model.PackageComponent
========
package org.byte_bloom.flux.domain.logic.pricing.decorator
>>>>>>>> origin/develop:src/main/kotlin/org/byte_bloom/flux/domain/logic/pricing/decorator/ExpressInsuranceDecorator.kt

private const val EXPRESS_INSURANCE_PREMIUM = 25.0

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
}
