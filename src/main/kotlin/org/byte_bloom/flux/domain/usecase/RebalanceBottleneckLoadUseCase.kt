package org.byte_bloom.flux.domain.usecase

class RebalanceBottleneckLoadUseCase {

    operator fun invoke(
        packageCount: Int,
        bottleneckLoadFactor: Double,
        alternativeLoadFactor: Double
    ): Int {
        val transferRatio = ((bottleneckLoadFactor - alternativeLoadFactor) / 2)
            .coerceIn(0.0, 1.0)

        return (packageCount * transferRatio).toInt()
    }
}