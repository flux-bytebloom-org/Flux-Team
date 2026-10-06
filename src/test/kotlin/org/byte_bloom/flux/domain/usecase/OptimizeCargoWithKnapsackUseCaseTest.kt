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

    @Test
    fun `does not select a package heavier than capacity`() {
        // Given
        val packageA = createPackage("PKG-A", weight = 11.0, priority = Priority.URGENT)

        // When
        val result = optimizeCargo(listOf(packageA), capacity = 10.0)

        // Then
        assertEquals(emptyList<Package>(), result)
    }

    @Test
    fun `does not exceed capacity when combined weight is too heavy`() {
        // Given
        val packageA = createPackage("PKG-A", weight = 6.0, priority = Priority.URGENT)
        val packageB = createPackage("PKG-B", weight = 5.0, priority = Priority.STANDARD)

        // When
        val result = optimizeCargo(listOf(packageA, packageB), capacity = 10.0)

        // Then
        assertEquals(listOf(packageA), result)
    }

    @Test
    fun `chooses the combination with the highest total priority instead of greedy first fit`() {
        // Given
        val packageA = createPackage("PKG-A", weight = 6.0, priority = Priority.URGENT)
        val packageB = createPackage("PKG-B", weight = 5.0, priority = Priority.STANDARD)
        val packageC = createPackage("PKG-C", weight = 5.0, priority = Priority.URGENT)

        // When
        val result = optimizeCargo(listOf(packageA, packageB, packageC), capacity = 10.0)

        // Then
        assertEquals(listOf(packageB, packageC), result)
    }

    @Test
    fun `ignores a package with missing weight without affecting the other selections`() {
        // Given
        val packageA = createPackage("PKG-A", weight = 5.0, priority = Priority.URGENT)
        val missingWeight = createPackage("PKG-NULL", weight = null, priority = Priority.URGENT)
        val packageC = createPackage("PKG-C", weight = 5.0, priority = Priority.URGENT)

        // When
        val result = optimizeCargo(listOf(packageA, missingWeight, packageC), capacity = 10.0)

        // Then
        assertEquals(listOf(packageA, packageC), result)
    }

    @Test
    fun `ignores a package with negative weight`() {
        // Given
        val negative = createPackage("PKG-NEG", weight = -2.0, priority = Priority.URGENT)
        val valid = createPackage("PKG-OK", weight = 3.0, priority = Priority.LOW)

        // When
        val result = optimizeCargo(listOf(negative, valid), capacity = 10.0)

        // Then
        assertEquals(listOf(valid), result)
    }

    @Test
    fun `never exceeds capacity when weights are fractional`() {
        // Given: 5.5 + 4.6 = 10.1 kg, more than the 10 kg capacity
        val packageA = createPackage("PKG-1", weight = 5.5, priority = Priority.URGENT)
        val packageB = createPackage("PKG-2", weight = 4.6, priority = Priority.STANDARD)

        // When
        val result = optimizeCargo(listOf(packageA, packageB), capacity = 10.0)

        // Then
        assertEquals(listOf(packageA), result)
    }

    @Test
    fun `returns empty list when capacity is negative`() {
        // Given
        val packageA = createPackage("PKG-A", weight = 5.0, priority = Priority.URGENT)

        // When
        val result = optimizeCargo(listOf(packageA), capacity = -3.0)

        // Then
        assertEquals(emptyList<Package>(), result)
    }

    @Test
    fun `returns empty list when capacity is zero and the package has weight`() {
        // Given
        val packageA = createPackage("PKG-A", weight = 5.0, priority = Priority.URGENT)

        // When
        val result = optimizeCargo(listOf(packageA), capacity = 0.0)

        // Then
        assertEquals(emptyList<Package>(), result)
    }
}

