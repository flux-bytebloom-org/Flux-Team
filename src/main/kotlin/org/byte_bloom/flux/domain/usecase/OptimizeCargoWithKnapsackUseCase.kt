package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package

class OptimizeCargoWithKnapsackUseCase {

    operator fun invoke(packages: List<Package>, capacity: Double): List<Package> {
        return packages.filter { (it.weight ?: 0.0) <= capacity }
    }
}