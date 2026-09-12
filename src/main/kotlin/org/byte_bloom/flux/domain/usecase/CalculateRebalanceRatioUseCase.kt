package org.byte_bloom.flux.domain.usecase

class CalculateRebalanceRatioUseCase {

    operator fun invoke(
        bottleneckLoadFactor: Double,
        alternativeLoadFactor: Double
    ): Double {

        return ((bottleneckLoadFactor - alternativeLoadFactor) / LOAD_FACTOR_SPLIT_DIVISOR)
            .coerceIn(MIN_SPLIT_RATIO, MAX_SPLIT_RATIO)
    }

    companion object {
        private const val MIN_SPLIT_RATIO = 0.0
        private const val MAX_SPLIT_RATIO = 0.6
        private const val LOAD_FACTOR_SPLIT_DIVISOR = 2
    }
}
