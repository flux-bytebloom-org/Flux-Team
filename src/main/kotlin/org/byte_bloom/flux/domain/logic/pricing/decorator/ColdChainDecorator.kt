<<<<<<<< HEAD:src/main/kotlin/org/byte_bloom/flux/domain/logic/decorator/ColdChainDecorator.kt
package org.byte_bloom.flux.domain.logic.decorator

import org.byte_bloom.flux.domain.model.PackageComponent
========
package org.byte_bloom.flux.domain.logic.pricing.decorator
>>>>>>>> origin/develop:src/main/kotlin/org/byte_bloom/flux/domain/logic/pricing/decorator/ColdChainDecorator.kt

private const val COLD_CHAIN_MULTIPLIER = 1.25

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
}
