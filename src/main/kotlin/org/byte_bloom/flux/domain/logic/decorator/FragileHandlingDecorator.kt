package org.byte_bloom.flux.domain.logic.decorator

import org.byte_bloom.flux.domain.model.PackageComponent

private const val FRAGILE_HANDLING_FEE = 20.0

class FragileHandlingDecorator(
    wrappedPackage: PackageComponent
) : PackageDecorator(wrappedPackage) {

    override fun getCost(): Double =
        wrappedPackage.getCost() + FRAGILE_HANDLING_FEE

    override fun getDescription(): String =
        "${wrappedPackage.getDescription()} + Fragile Handling"
}
