package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package

class OptimizeCargoWithKnapsackUseCase {

    operator fun invoke(packages: List<Package>, capacity: Double): List<Package> {
        var remainingCapacity = capacity
        val selected = mutableListOf<Package>()

        for (pkg in packages) {
            val weight = pkg.weight ?: 0.0
            if (weight <= remainingCapacity) {
                selected.add(pkg)
                remainingCapacity -= weight
            }
        }
        return selected
    }
}