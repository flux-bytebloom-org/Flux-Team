package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority

private const val DEFAULT_URGENCY_THRESHOLD = 0.2

class ClassifyTripUrgencyUseCase {
    operator fun invoke(
        packages: List<Package>,
        urgencyThreshold: Double = DEFAULT_URGENCY_THRESHOLD
    ): Boolean {
        if (packages.isEmpty()) return false
        val urgentRatio = packages.count { it.priority == Priority.URGENT }
            .toDouble() / packages.size
        return urgentRatio >= urgencyThreshold
    }
}
