package org.byte_bloom.flux.domain.usecase

private const val MIN_SPLIT_RATIO = 0.0
private const val MAX_SPLIT_RATIO = 0.6

class CalculateRebalanceRatioUseCase {

    operator fun invoke(
        bottleneckLoadFactor: Double,
        alternativeLoadFactor: Double
    ): Double {

        return ((bottleneckLoadFactor - alternativeLoadFactor) / 2)
            .coerceIn(MIN_SPLIT_RATIO, MAX_SPLIT_RATIO)
    }
}
