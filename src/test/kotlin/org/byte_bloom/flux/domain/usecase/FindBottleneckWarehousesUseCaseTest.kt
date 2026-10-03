package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.response.WeightedPath
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.assertThrows

class FindBottleneckWarehousesUseCaseTest {

    private val useCase = FindBottleneckWarehousesUseCase()

    private fun warehouse(id: String) =
        Warehouse(
            id = id,
            name = id,
            regionalZone = RegionalZone.CENTRAL,
            latitude = 0.0,
            longitude = 0.0
        )

    @Test
    fun `should calculate transit load for intermediate warehouse`() {
        // Given
        val w1 = warehouse("W1")
        val w2 = warehouse("W2")
        val w3 = warehouse("W3")

        val weightedPaths = listOf(
            WeightedPath(path = listOf(w1, w2, w3), packageCount = 5)
        )

        // When
        val result = useCase(weightedPaths, minTransitLoad = 1)

        // Then
        assertEquals(1, result.size)
        assertEquals(w2, result[0].warehouse)
        assertEquals(5, result[0].transitLoad)
    }

    @Test
    fun `should sum loads when warehouse appears in multiple paths`() {
        // Given
        val w1 = warehouse("W1")
        val w2 = warehouse("W2")
        val w3 = warehouse("W3")
        val w4 = warehouse("W4")

        val weightedPaths = listOf(
            WeightedPath(listOf(w1, w2, w3), 5),
            WeightedPath(listOf(w4, w2, w3), 3)
        )

        // When
        val result = useCase(weightedPaths, minTransitLoad = 1)

        // Then
        assertEquals(1, result.size)
        assertEquals(w2, result[0].warehouse)
        assertEquals(8, result[0].transitLoad)
    }

    @Test
    fun `should sort bottlenecks by descending transit load`() {
        // Given
        val w1 = warehouse("W1")
        val w2 = warehouse("W2")
        val w3 = warehouse("W3")
        val w4 = warehouse("W4")

        val weightedPaths = listOf(
            WeightedPath(listOf(w1, w2, w3), 3),
            WeightedPath(listOf(w1, w4, w3), 7)
        )

        // When
        val result = useCase(weightedPaths, minTransitLoad = 1)

        // Then
        assertEquals(2, result.size)
        assertEquals(w4, result[0].warehouse)
        assertEquals(7, result[0].transitLoad)
        assertEquals(w2, result[1].warehouse)
        assertEquals(3, result[1].transitLoad)
    }

    @Test
    fun `should throw exception for negative minimum transit load`() {
        // Given
        val weightedPaths = emptyList<WeightedPath>()

        // When & Then
        assertThrows<LogisticsException.ValidationException.InvalidTransitLoadException> {
            useCase(weightedPaths, minTransitLoad = -1)
        }
    }

    @Test
    fun `should return empty list when no warehouse is intermediate`() {
        // Given
        val w1 = warehouse("W1")
        val w2 = warehouse("W2")

        val weightedPaths = listOf(
            WeightedPath(listOf(w1, w2), 5)
        )

        // When
        val result = useCase(weightedPaths, minTransitLoad = 1)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return empty list when threshold is above all loads`() {
        // Given
        val w1 = warehouse("W1")
        val w2 = warehouse("W2")
        val w3 = warehouse("W3")

        val weightedPaths = listOf(
            WeightedPath(listOf(w1, w2, w3), 5)
        )

        // When
        val result = useCase(weightedPaths, minTransitLoad = 6)

        // Then
        assertTrue(result.isEmpty())
    }
}