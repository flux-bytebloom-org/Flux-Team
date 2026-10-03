package org.byte_bloom.flux.domain.usecase

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.testdata.createTestPackage
import org.byte_bloom.flux.domain.testdata.aVehicle
import org.byte_bloom.flux.domain.testdata.createTestWarehouse
import org.junit.jupiter.api.Test

class FindSmallestFitVehicleUseCaseTest {

    private val useCase = FindSmallestFitVehicleUseCase()

    @Test
    fun `given several vehicles that fit when find then returns the one with smallest capacity`() {
        // Given
        val warehouse = createTestWarehouse()
        val tooSmall = aVehicle(id = "V-1", capacity = 20.0)
        val smallestFit = aVehicle(id = "V-2", capacity = 60.0)
        val large = aVehicle(id = "V-3", capacity = 500.0)
        warehouse.addVehicle(large)
        warehouse.addVehicle(tooSmall)
        warehouse.addVehicle(smallestFit)
        val pkg = createTestPackage(weight = 50.0)

        // When
        val result = useCase(warehouse, pkg)

        // Then
        assertThat(result).isEqualTo(smallestFit)
    }

    @Test
    fun `given vehicle capacity equal to package weight when find then returns that vehicle`() {
        // Given
        val warehouse = createTestWarehouse()
        val exactVehicle = aVehicle(capacity = 50.0)
        warehouse.addVehicle(exactVehicle)
        val pkg = createTestPackage(weight = 50.0)

        // When
        val result = useCase(warehouse, pkg)

        // Then
        assertThat(result).isEqualTo(exactVehicle)
    }

    @Test
    fun `given no vehicle can carry the package when find then returns null`() {
        // Given
        val warehouse = createTestWarehouse()
        warehouse.addVehicle(aVehicle(capacity = 20.0))
        val pkg = createTestPackage(weight = 50.0)

        // When
        val result = useCase(warehouse, pkg)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `given warehouse without vehicles when find then returns null`() {
        // Given
        val warehouse = createTestWarehouse()
        val pkg = createTestPackage(weight = 10.0)

        // When
        val result = useCase(warehouse, pkg)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `given package with null weight when find then treats weight as zero and returns smallest vehicle`() {
        // Given
        val warehouse = createTestWarehouse()
        val small = aVehicle(id = "V-1", capacity = 10.0)
        val large = aVehicle(id = "V-2", capacity = 100.0)
        warehouse.addVehicle(large)
        warehouse.addVehicle(small)
        val pkg = createTestPackage(weight = null)

        // When
        val result = useCase(warehouse, pkg)

        // Then
        assertThat(result).isEqualTo(small)
    }
}
