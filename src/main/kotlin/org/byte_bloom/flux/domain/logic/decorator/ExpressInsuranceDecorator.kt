package org.byte_bloom.flux.domain.logic.decorator

import org.byte_bloom.flux.domain.model.PackageComponent

private const val EXPRESS_INSURANCE_PREMIUM = 25.0

class ExpressInsuranceDecorator(
    wrappedPackage: PackageComponent
) : PackageDecorator(wrappedPackage) {

    override fun getCost(): Double =
        wrappedPackage.getCost() + EXPRESS_INSURANCE_PREMIUM

    override fun getDescription(): String =
        "${wrappedPackage.getDescription()} + Express Insurance"
}
