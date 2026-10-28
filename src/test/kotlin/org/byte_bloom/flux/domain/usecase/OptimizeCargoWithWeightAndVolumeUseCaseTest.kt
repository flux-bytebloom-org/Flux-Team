package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private const val WEIGHT_CAPACITY_KG = 10.0
private const val VOLUME_CAPACITY_M3 = 1.0
private const val NEGATIVE_CAPACITY = -1.0

private const val LIGHT_WEIGHT_KG = 1.0
private const val SMALL_VOLUME_M3 = 0.1
private const val OVERWEIGHT_KG = 11.0
private const val OVERSIZED_VOLUME_M3 = 1.5
private const val NEGATIVE_VOLUME_M3 = -0.5
private const val DENSE_WEIGHT_KG = 6.0

private const val DECIMAL_PACKAGE_COUNT = 3
private const val DECIMAL_VOLUME_M3 = 0.3
private const val DECIMAL_VOLUME_CAPACITY_M3 = 0.9

private const val BULKY_WEIGHT_KG = 1.0
private const val BULKY_VOLUME_M3 = 0.6
private const val STEEL_WEIGHT_KG = 9.0
private const val STEEL_VOLUME_M3 = 0.1
private const val ENGINE_WEIGHT_KG = 8.0
private const val ENGINE_VOLUME_M3 = 0.2

private const val HUB_LATITUDE = 0.0
private const val HUB_LONGITUDE = 0.0

class OptimizeCargoWithWeightAndVolumeUseCaseTest {

    private val optimizeWithVolume = OptimizeCargoWithWeightAndVolumeUseCase()
    private val optimizeWeightOnly = OptimizeCargoWithKnapsackUseCase()
    private val hub = Warehouse("WH-TEST", "Test Warehouse", RegionalZone.NORTH, HUB_LATITUDE, HUB_LONGITUDE)

    private fun createPackage(id: String, weight: Double?, volumeM3: Double, priority: Priority) = Package(
        id = id,
        weight = weight,
        originHub = hub,
        destinationHub = hub,
        priority = priority,
        volumeM3 = volumeM3
    )

    @Test
    fun `returns empty list when there are no packages`() {
        // Given
        val packages = emptyList<Package>()

        // When
        val result = optimizeWithVolume(packages, WEIGHT_CAPACITY_KG, VOLUME_CAPACITY_M3)

        // Then
        assertEquals(emptyList<Package>(), result)
    }

    @Test
    fun `returns empty list when volume capacity is negative`() {
        // Given
        val packageA = createPackage("PKG-A", LIGHT_WEIGHT_KG, SMALL_VOLUME_M3, Priority.URGENT)

        // When
        val result = optimizeWithVolume(listOf(packageA), WEIGHT_CAPACITY_KG, NEGATIVE_CAPACITY)

        // Then
        assertEquals(emptyList<Package>(), result)
    }

    @Test
    fun `selects a package when weight and volume exactly match capacity`() {
        // Given
        val packageA = createPackage("PKG-A", WEIGHT_CAPACITY_KG, VOLUME_CAPACITY_M3, Priority.URGENT)

        // When
        val result = optimizeWithVolume(listOf(packageA), WEIGHT_CAPACITY_KG, VOLUME_CAPACITY_M3)

        // Then
        assertEquals(listOf(packageA), result)
    }

    @Test
    fun `does not select a light package that is too bulky`() {
        // Given
        val bulkyPackage = createPackage("PKG-BULKY", LIGHT_WEIGHT_KG, OVERSIZED_VOLUME_M3, Priority.URGENT)

        // When
        val result = optimizeWithVolume(listOf(bulkyPackage), WEIGHT_CAPACITY_KG, VOLUME_CAPACITY_M3)

        // Then
        assertEquals(emptyList<Package>(), result)
    }

    @Test
    fun `does not select a small package that is too heavy`() {
        // Given
        val densePackage = createPackage("PKG-DENSE", OVERWEIGHT_KG, SMALL_VOLUME_M3, Priority.URGENT)

        // When
        val result = optimizeWithVolume(listOf(densePackage), WEIGHT_CAPACITY_KG, VOLUME_CAPACITY_M3)

        // Then
        assertEquals(emptyList<Package>(), result)
    }

    @Test
    fun `skips packages with negative volume`() {
        // Given
        val invalidPackage = createPackage("PKG-BAD", LIGHT_WEIGHT_KG, NEGATIVE_VOLUME_M3, Priority.URGENT)

        // When
        val result = optimizeWithVolume(listOf(invalidPackage), WEIGHT_CAPACITY_KG, VOLUME_CAPACITY_M3)

        // Then
        assertEquals(emptyList<Package>(), result)
    }

    @Test
    fun `still respects the weight limit when there is plenty of volume`() {
        // Given
        val denseUrgent = createPackage("PKG-DENSE-1", DENSE_WEIGHT_KG, SMALL_VOLUME_M3, Priority.URGENT)
        val denseStandard = createPackage("PKG-DENSE-2", DENSE_WEIGHT_KG, SMALL_VOLUME_M3, Priority.STANDARD)

        // When
        val result = optimizeWithVolume(listOf(denseUrgent, denseStandard), WEIGHT_CAPACITY_KG, VOLUME_CAPACITY_M3)

        // Then
        assertEquals(listOf(denseUrgent), result)
    }

    @Test
    fun `fits decimal volumes exactly without rounding errors`() {
        // Given
        val packages = (1..DECIMAL_PACKAGE_COUNT).map { index ->
            createPackage("PKG-$index", LIGHT_WEIGHT_KG, DECIMAL_VOLUME_M3, Priority.STANDARD)
        }

        // When
        val result = optimizeWithVolume(packages, WEIGHT_CAPACITY_KG, DECIMAL_VOLUME_CAPACITY_M3)

        // Then
        assertEquals(packages, result)
    }

    @Test
    fun `volume limit swaps bulky low-weight packages for dense high-weight ones`() {
        // Given
        val bulkyUrgent = createPackage("PKG-PILLOWS", BULKY_WEIGHT_KG, BULKY_VOLUME_M3, Priority.URGENT)
        val bulkyStandard = createPackage("PKG-BLANKETS", BULKY_WEIGHT_KG, BULKY_VOLUME_M3, Priority.STANDARD)
        val denseStandard = createPackage("PKG-STEEL", STEEL_WEIGHT_KG, STEEL_VOLUME_M3, Priority.STANDARD)
        val denseLow = createPackage("PKG-ENGINE", ENGINE_WEIGHT_KG, ENGINE_VOLUME_M3, Priority.LOW)
        val packages = listOf(bulkyUrgent, bulkyStandard, denseStandard, denseLow)

        // When
        val weightOnly = optimizeWeightOnly(packages, WEIGHT_CAPACITY_KG)
        val weightAndVolume = optimizeWithVolume(packages, WEIGHT_CAPACITY_KG, VOLUME_CAPACITY_M3)

        // Then
        assertEquals(listOf(bulkyUrgent, bulkyStandard, denseLow), weightOnly)
        assertEquals(listOf(bulkyUrgent, denseStandard), weightAndVolume)
    }
}
