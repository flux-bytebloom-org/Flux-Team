package org.byte_bloom.flux.domain.logic.pricing.decorator

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

