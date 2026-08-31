package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package

class ExtractUniqueShipmentRoutesUseCase {
    operator fun invoke(packages: List<Package>): Map<Pair<String, String>, Int> {
        return packages
            .groupBy { it.originHub.id to it.destinationHub.id }
            .mapValues { entry -> entry.value.size }
    }

}