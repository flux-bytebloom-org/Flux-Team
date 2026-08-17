package org.byte_bloom.flux.domain.logic.pricing

abstract class PackageDecorator(
    protected val packageComponent: PackageComponent
) : PackageComponent {

    override fun calculateTransitRate(baseRate: Double): Double {
        return packageComponent.calculateTransitRate(baseRate)
    }
}