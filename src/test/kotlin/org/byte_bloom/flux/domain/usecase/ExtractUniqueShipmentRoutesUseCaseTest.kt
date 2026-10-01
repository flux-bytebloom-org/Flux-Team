package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExtractUniqueShipmentRoutesUseCaseTest {

    private val useCase = ExtractUniqueShipmentRoutesUseCase()

    private fun warehouse(id: String) =
        Warehouse(
            id = id,
            name = id,
            regionalZone = RegionalZone.CENTRAL,
            latitude = 0.0,
            longitude = 0.0
        )

    private fun packageItem(
        id: String,
        origin: Warehouse,
        destination: Warehouse
    ) =
        Package(
            id = id,
            weight = 10.0,
            originHub = origin,
            destinationHub = destination,
            priority = Priority.STANDARD
        )

    @Test
    fun `should group packages with same route`() {
        // Given
        val w1 = warehouse("W1")
        val w2 = warehouse("W2")

        val packages = listOf(
            packageItem("P1", w1, w2),
            packageItem("P2", w1, w2)
        )

        // When
        val result = useCase(packages)

        // Then
        assertEquals(1, result.size)
        assertEquals(2, result[w1.id to w2.id])
    }

    @Test
    fun `should keep different routes separate`() {
        // Given
        val w1 = warehouse("W1")
        val w2 = warehouse("W2")
        val w3 = warehouse("W3")

        val packages = listOf(
            packageItem("P1", w1, w2),
            packageItem("P2", w1, w3)
        )

        // When
        val result = useCase(packages)

        // Then
        assertEquals(2, result.size)
        assertEquals(1, result[w1.id to w2.id])
        assertEquals(1, result[w1.id to w3.id])
    }

    @Test
    fun `should return empty map when packages are empty`() {
        // Given
        val packages = emptyList<Package>()

        // When
        val result = useCase(packages)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return count one for single package`() {
        // Given
        val w1 = warehouse("W1")
        val w2 = warehouse("W2")

        val packages = listOf(
            packageItem("P1", w1, w2)
        )

        // When
        val result = useCase(packages)

        // Then
        assertEquals(1, result[w1.id to w2.id])
    }
}