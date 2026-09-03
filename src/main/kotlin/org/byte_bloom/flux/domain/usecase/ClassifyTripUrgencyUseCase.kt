package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority

class ClassifyTripUrgencyUseCase {
    operator fun invoke(
        packages: List<Package>,
        urgencyThreshold: Double = 0.2
    ): Boolean {
        if (packages.isEmpty()) return false
        val urgentRatio = packages.count { it.priority == Priority.URGENT }
            .toDouble() / packages.size
        return urgentRatio >= urgencyThreshold
    }
}
