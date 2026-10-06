package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OptimizeCargoWithKnapsackUseCaseTest {

    private val optimizeCargo = OptimizeCargoWithKnapsackUseCase()
    private val hub = Warehouse("WH-TEST", "Test Warehouse", RegionalZone.NORTH, 0.0, 0.0)

    private fun createPackage(id: String, weight: Double?, priority: Priority) = Package(
        id = id,
        weight = weight,
        originHub = hub,
        destinationHub = hub,
        priority = priority
    )

    @Test
    fun `returns empty list when there are no packages`() {
        // Given
        val packages = emptyList<Package>()

        // When
        val result = optimizeCargo(packages, capacity = 10.0)

        // Then
        assertEquals(emptyList<Package>(), result)
    }

    @Test
    fun `selects a package when its weight exactly matches capacity`() {
        // Given
        val packageA = createPackage("PKG-A", weight = 10.0, priority = Priority.URGENT)

        // When
        val result = optimizeCargo(listOf(packageA), capacity = 10.0)

        // Then
        assertEquals(listOf(packageA), result)
    }
}

