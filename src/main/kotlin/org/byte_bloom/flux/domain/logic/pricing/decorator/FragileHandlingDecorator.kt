<<<<<<<< HEAD:src/main/kotlin/org/byte_bloom/flux/domain/logic/decorator/FragileHandlingDecorator.kt
package org.byte_bloom.flux.domain.logic.decorator

import org.byte_bloom.flux.domain.model.PackageComponent
========
package org.byte_bloom.flux.domain.logic.pricing.decorator
>>>>>>>> origin/develop:src/main/kotlin/org/byte_bloom/flux/domain/logic/pricing/decorator/FragileHandlingDecorator.kt

private const val FRAGILE_PROTECTIVE_FEE = 50.0

class FragileHandlingDecorator(
    packageComponent: PackageComponent
) : PackageDecorator(packageComponent) {

    override fun getDescription(): String {
        return "${packageComponent.getDescription()} + Fragile Handling"
    }

    override fun calculateTransitRate(baseRate: Double): Double {
        return super.calculateTransitRate(baseRate) +
                FRAGILE_PROTECTIVE_FEE
    }
}
