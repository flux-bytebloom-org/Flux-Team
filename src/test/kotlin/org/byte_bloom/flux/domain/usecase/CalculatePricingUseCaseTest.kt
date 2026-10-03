package org.byte_bloom.flux.domain.usecase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.byte_bloom.flux.domain.algorithm.pricing.RoutePricingEngine
import org.byte_bloom.flux.domain.testdata.createTestPackage
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

private const val PACKAGE_WEIGHT_KG = 10.0
private const val HEAVY_PACKAGE_WEIGHT_KG = 25.0
private const val NO_WEIGHT_KG = 0.0

private const val DISTANCE_KM = 100.0
private const val SHORT_DISTANCE_KM = 40.0
private const val MEDIUM_DISTANCE_KM = 80.0
private const val ZERO_DISTANCE_KM = 0.0
private const val NEGATIVE_DISTANCE_KM = -50.0

private const val ENGINE_COST = 1500.0
private const val ZERO_COST = 0.0
private const val NEGATIVE_COST = -750.0

private const val PRICING_FAILURE_MESSAGE = "Pricing failure"

class CalculatePricingUseCaseTest {

    private val pricingEngine = mockk<RoutePricingEngine>()
    private val useCase = CalculatePricingUseCase(pricingEngine)

    @Test
    fun `given package with weight when calculate pricing then returns engine cost`() {
        // Given
        val pkg = createTestPackage(weight = PACKAGE_WEIGHT_KG)
        every { pricingEngine.calculateTransitCost(DISTANCE_KM, PACKAGE_WEIGHT_KG) } returns ENGINE_COST

        // When
        val result = useCase(pkg, distanceKm = DISTANCE_KM)

        // Then
        assertThat(result).isEqualTo(ENGINE_COST)
    }

    @Test
    fun `given package with weight when calculate pricing then passes distance and weight to engine`() {
        // Given
        val pkg = createTestPackage(weight = HEAVY_PACKAGE_WEIGHT_KG)
        every { pricingEngine.calculateTransitCost(any(), any()) } returns ZERO_COST

        // When
        useCase(pkg, distanceKm = SHORT_DISTANCE_KM)

        // Then
        verify(exactly = 1) { pricingEngine.calculateTransitCost(SHORT_DISTANCE_KM, HEAVY_PACKAGE_WEIGHT_KG) }
    }

    @Test
    fun `given engine throws when calculate pricing then exception propagates`() {
        // Given
        val pkg = createTestPackage(weight = PACKAGE_WEIGHT_KG)
        every { pricingEngine.calculateTransitCost(any(), any()) } throws
                IllegalStateException(PRICING_FAILURE_MESSAGE)

        // When
        val exception = assertFailsWith<IllegalStateException> { useCase(pkg, distanceKm = DISTANCE_KM) }

        // Then
        assertThat(exception).hasMessageThat().isEqualTo(PRICING_FAILURE_MESSAGE)
    }

    @Test
    fun `given package with null weight when calculate pricing then uses zero weight`() {
        // Given
        val pkg = createTestPackage(weight = null)
        every { pricingEngine.calculateTransitCost(MEDIUM_DISTANCE_KM, NO_WEIGHT_KG) } returns ZERO_COST

        // When
        val result = useCase(pkg, distanceKm = MEDIUM_DISTANCE_KM)

        // Then
        assertThat(result).isEqualTo(ZERO_COST)
        verify(exactly = 1) { pricingEngine.calculateTransitCost(MEDIUM_DISTANCE_KM, NO_WEIGHT_KG) }
    }

    @Test
    fun `given zero distance when calculate pricing then returns engine cost for zero distance`() {
        // Given
        val pkg = createTestPackage(weight = PACKAGE_WEIGHT_KG)
        every { pricingEngine.calculateTransitCost(ZERO_DISTANCE_KM, PACKAGE_WEIGHT_KG) } returns ZERO_COST

        // When
        val result = useCase(pkg, distanceKm = ZERO_DISTANCE_KM)

        // Then
        assertThat(result).isEqualTo(ZERO_COST)
    }

    @Test
    fun `given negative distance when calculate pricing then passes it to engine without validation`() {
        // Given
        val pkg = createTestPackage(weight = PACKAGE_WEIGHT_KG)
        every { pricingEngine.calculateTransitCost(NEGATIVE_DISTANCE_KM, PACKAGE_WEIGHT_KG) } returns NEGATIVE_COST

        // When
        val result = useCase(pkg, distanceKm = NEGATIVE_DISTANCE_KM)

        // Then
        assertThat(result).isEqualTo(NEGATIVE_COST)
        verify(exactly = 1) { pricingEngine.calculateTransitCost(NEGATIVE_DISTANCE_KM, PACKAGE_WEIGHT_KG) }
    }
}
