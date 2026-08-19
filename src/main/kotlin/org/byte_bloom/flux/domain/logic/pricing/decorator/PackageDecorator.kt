<<<<<<<< HEAD:src/main/kotlin/org/byte_bloom/flux/domain/logic/decorator/PackageDecorator.kt
package org.byte_bloom.flux.domain.logic.decorator

import org.byte_bloom.flux.domain.model.PackageComponent
========
package org.byte_bloom.flux.domain.logic.pricing.decorator
>>>>>>>> origin/develop:src/main/kotlin/org/byte_bloom/flux/domain/logic/pricing/decorator/PackageDecorator.kt

abstract class PackageDecorator(
    protected val packageComponent: PackageComponent
) : PackageComponent {

    override fun getDescription(): String {
        return packageComponent.getDescription()
    }

    override fun calculateTransitRate(baseRate: Double): Double {
        return packageComponent.calculateTransitRate(baseRate)
    }
}
