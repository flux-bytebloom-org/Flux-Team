package org.byte_bloom.flux.domain.logic.decorator

import org.byte_bloom.flux.domain.model.PackageComponent


abstract class PackageDecorator(
    protected val wrappedPackage: PackageComponent
) : PackageComponent by wrappedPackage
