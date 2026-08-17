package org.byte_bloom.flux.domain.logic.Decorator

import org.byte_bloom.flux.domain.model.PackageComponent

abstract class PackageDecorator(
    protected val packageComponent: PackageComponent
) : PackageComponent {

    override fun calculateTransitRate(baseRate: Double): Double {
        return packageComponent.calculateTransitRate(baseRate)
    }
}
