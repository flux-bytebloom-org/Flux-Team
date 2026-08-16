package org.byte_bloom.flux.domain.logic.decorator

import org.byte_bloom.flux.domain.model.PackageComponent

private const val COLD_CHAIN_MULTIPLIER = 1.3

class ColdChainDecorator(
    wrappedPackage: PackageComponent
) : PackageDecorator(wrappedPackage) {

    override fun getCost(): Double =
        wrappedPackage.getCost() * COLD_CHAIN_MULTIPLIER


    override fun getDescription(): String =
        "${wrappedPackage.getDescription()} + Cold Chain"
}
