package org.byte_bloom.flux.domain.usecase

class CalculateRebalanceRatioUseCase {

    operator fun invoke(
        bottleneckLoadFactor: Double,
        alternativeLoadFactor: Double
    ): Double {

        return ((bottleneckLoadFactor - alternativeLoadFactor) / 2)
            .coerceIn(MIN_SPLIT_RATIO, MAX_SPLIT_RATIO)
    }

    private companion object {
        const val MIN_SPLIT_RATIO = 0.0
        const val MAX_SPLIT_RATIO = 0.6
    }
}
