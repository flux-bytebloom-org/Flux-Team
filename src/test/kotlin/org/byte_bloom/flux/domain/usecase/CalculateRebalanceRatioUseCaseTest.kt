package org.byte_bloom.flux.domain.usecase

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat

class CalculateRebalanceRatioUseCaseTest {

    val useCase = CalculateRebalanceRatioUseCase()


    @Test
    fun `returns half of the load difference when it is within the allowed range`() {
        // When
        val ratio = useCase(bottleneckLoadFactor = 1.0, alternativeLoadFactor = 0.6)

        // Then
        assertThat(ratio).isWithin(EPSILON).of(0.2)
    }

    @Test
    fun `returns zero when both loads are equal`() {
        // When
        val ratio = useCase(bottleneckLoadFactor = 0.5, alternativeLoadFactor = 0.5)

        // Then
        assertThat(ratio).isWithin(EPSILON).of(0.0)
    }

    @Test
    fun `clamps to zero when the alternative is more loaded than the bottleneck`() {
        // When
        val ratio = useCase(bottleneckLoadFactor = 0.3, alternativeLoadFactor = 0.5)

        // Then
        assertThat(ratio).isWithin(EPSILON).of(0.0)
    }

    @Test
    fun `returns exactly the cap when half the difference equals 0_6`() {
        // When
        val ratio = useCase(bottleneckLoadFactor = 1.2, alternativeLoadFactor = 0.0)

        // Then
        assertThat(ratio).isWithin(EPSILON).of(0.6)
    }

    @Test
    fun `clamps to 0_6 when half the difference exceeds the cap`() {
        // When
        val ratio = useCase(bottleneckLoadFactor = 2.0, alternativeLoadFactor = 0.0)

        // Then
        assertThat(ratio).isWithin(EPSILON).of(0.6)
    }

    private companion object {
        const val EPSILON = 1e-9
    }



}
