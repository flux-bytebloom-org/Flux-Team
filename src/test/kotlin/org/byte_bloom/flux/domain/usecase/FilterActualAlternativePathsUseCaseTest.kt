package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.response.WeightedPath
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FilterActualAlternativePathsUseCaseTest {

    private val useCase = FilterActualAlternativePathsUseCase()

    private fun warehouse(id: String) =
        Warehouse(
            id = id,
            name = id,
            regionalZone = RegionalZone.CENTRAL,
            latitude = 0.0,
            longitude = 0.0
        )

    @Test
    fun `should return path when bottleneck is intermediate and alternative is different`() {
        // Given
        val w1 = warehouse("W1")
        val bottleneck = warehouse("W2")
        val w3 = warehouse("W3")
        val alternative = warehouse("W4")

        val weightedPath = WeightedPath(
            path = listOf(w1, bottleneck, w3),
            packageCount = 5
        )

        val alternatives = mapOf("W1" to "W3" to listOf(w1, alternative, w3))

        // When
        val result = useCase(
            listOf(weightedPath),
            bottleneck,
            alternatives
        )

        // Then
        assertEquals(1, result.size)
        assertEquals(weightedPath, result[0].first)
        assertEquals(listOf(w1, alternative, w3), result[0].second)
    }

    @Test
    fun `should exclude path when bottleneck is not intermediate`() {
        // Given
        val bottleneck = warehouse("W1")
        val w2 = warehouse("W2")
        val w3 = warehouse("W3")

        val weightedPath = WeightedPath(
            path = listOf(bottleneck, w2, w3),
            packageCount = 2
        )

        val alternatives = mapOf("W1" to "W3" to listOf(w2, w3))

        // When
        val result = useCase(
            listOf(weightedPath),
            bottleneck,
            alternatives
        )

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should exclude path when alternative is missing`() {
        // Given
        val w1 = warehouse("W1")
        val bottleneck = warehouse("W2")
        val w3 = warehouse("W3")

        val weightedPath = WeightedPath(
            listOf(w1, bottleneck, w3),
            3
        )

        // When
        val result = useCase(listOf(weightedPath), bottleneck, emptyMap() )

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should exclude path when alternative is empty`() {
        // Given
        val w1 = warehouse("W1")
        val bottleneck = warehouse("W2")
        val w3 = warehouse("W3")

        val weightedPath = WeightedPath(listOf(w1, bottleneck, w3), 3)

        val alternatives = mapOf(("W1" to "W3") to emptyList<Warehouse>())

        // When
        val result = useCase(listOf(weightedPath),bottleneck, alternatives)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should exclude path when alternative is identical to original`() {
        // Given
        val w1 = warehouse("W1")
        val bottleneck = warehouse("W2")
        val w3 = warehouse("W3")

        val originalPath = listOf(w1, bottleneck, w3)

        val weightedPath = WeightedPath(originalPath, 3)

        val alternatives = mapOf("W1" to "W3" to originalPath)

        // When
        val result = useCase(listOf(weightedPath), bottleneck, alternatives)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return empty list when all paths are empty`() {
        // Given
        val bottleneck = warehouse("W2")

        // When
        val result = useCase(emptyList(), bottleneck, emptyMap())

        // Then
        assertTrue(result.isEmpty())
    }
}