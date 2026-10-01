package org.byte_bloom.flux.domain.usecase

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GenerateWeightedShipmentPathsUseCaseTest {

    private val findOptimalPathUseCase = mockk<FindOptimalPathUseCase>()
    private val useCase =
        GenerateWeightedShipmentPathsUseCase(findOptimalPathUseCase)

    private fun warehouse(id: String) =
        Warehouse(
            id = id,
            name = id,
            regionalZone = RegionalZone.CENTRAL,
            latitude = 0.0,
            longitude = 0.0
        )

    @Test
    fun `should generate weighted path for valid route`() {
        // Given
        val origin = warehouse("W1")
        val destination = warehouse("W2")

        val path = listOf(origin, destination)

        every {
            findOptimalPathUseCase(origin, destination)
        } returns path

        val routes = mapOf("W1" to "W2" to 3)

        val warehouses = mapOf( "W1" to origin, "W2" to destination)

        // When
        val result = useCase(routes, warehouses)

        // Then
        assertEquals(1, result.size)
        assertEquals(path, result[0].path)
        assertEquals(3, result[0].packageCount)

        verify(exactly = 1) {
            findOptimalPathUseCase(origin, destination)
        }
    }

    @Test
    fun `should skip route when origin warehouse is missing`() {
        // Given
        val destination = warehouse("W2")

        val routes = mapOf(
            "W1" to "W2" to 2
        )

        val warehouses = mapOf(
            "W2" to destination
        )

        // When
        val result = useCase(routes, warehouses)

        // Then
        assertTrue(result.isEmpty())

        verify(exactly = 0) {
            findOptimalPathUseCase(any(), any())
        }
    }

    @Test
    fun `should skip route when destination warehouse is missing`() {
        // Given
        val origin = warehouse("W1")

        val routes = mapOf("W1" to "W2" to 2)

        val warehouses = mapOf("W1" to origin)

        // When
        val result = useCase(routes, warehouses)

        // Then
        assertTrue(result.isEmpty())

        verify(exactly = 0) {findOptimalPathUseCase(any(), any()) }
    }

    @Test
    fun `should skip route when optimal path is empty`() {
        // Given
        val origin = warehouse("W1")
        val destination = warehouse("W2")

        every {
            findOptimalPathUseCase(origin, destination)
        } returns emptyList()

        val routes = mapOf("W1" to "W2" to 2)

        val warehouses = mapOf("W1" to origin, "W2" to destination)

        // When
        val result = useCase(routes, warehouses)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return empty list when routes are empty`() {
        // Given
        val routes = emptyMap<Pair<String, String>, Int>()
        val warehouses = emptyMap<String, Warehouse>()

        // When
        val result = useCase(routes, warehouses)

        // Then
        assertTrue(result.isEmpty())

        verify(exactly = 0) {
            findOptimalPathUseCase(any(), any())
        }
    }
}