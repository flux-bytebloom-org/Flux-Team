package org.byte_bloom.flux.domain.usecase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.testdata.createTestPackage
import org.byte_bloom.flux.domain.testdata.aVehicle
import org.byte_bloom.flux.domain.testdata.createTestWarehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class AssignPackageToLowestCostStationedVehicleUseCaseTest {

    private val findVehicles: FindStationedVehiclesByCapacityUseCase = mockk()
    private val useCase = AssignPackageToLowestCostStationedVehicleUseCase(findVehicles)

    private val warehouse = createTestWarehouse("WH-1")

    @Test
    fun `given several eligible vehicles when assign then returns the one with lowest total cost`() {
        // Given
        val expensive = aVehicle(id = "V-1", costPerKm = 3.0)
        val cheapest = aVehicle(id = "V-2", costPerKm = 1.5)
        val medium = aVehicle(id = "V-3", costPerKm = 2.0)
        every { findVehicles(warehouse, 50.0) } returns listOf(expensive, cheapest, medium)
        val pkg = createTestPackage(weight = 50.0)

        // When
        val result = useCase(warehouse, pkg, 100.0)

        // Then
        assertThat(result).isEqualTo(cheapest)
    }

    @Test
    fun `given package without weight when assign then throws InvalidPackageWeightException and skips search`() {
        // Given
        val pkg = createTestPackage(weight = null)

        // When / Then
        assertFailsWith<LogisticsException.ValidationException.InvalidPackageWeightException> {
            useCase(warehouse, pkg, 100.0)
        }
        verify(exactly = 0) { findVehicles(any(), any()) }
    }

    @Test
    fun `given no eligible vehicles when assign then throws NoSuitableVehicleException with warehouse id`() {
        // Given
        every { findVehicles(warehouse, 50.0) } returns emptyList()
        val pkg = createTestPackage(weight = 50.0)

        // When
        val exception = assertFailsWith<LogisticsException.BusinessLogicException.NoSuitableVehicleException> {
            useCase(warehouse, pkg, 100.0)
        }

        // Then
        assertThat(exception.warehouseId).isEqualTo("WH-1")
    }

    @Test
    fun `given package weight when assign then asks finder with same warehouse and weight`() {
        // Given
        every { findVehicles(any(), any()) } returns listOf(aVehicle())
        val pkg = createTestPackage(weight = 42.0)

        // When
        useCase(warehouse, pkg, 10.0)

        // Then
        verify(exactly = 1) { findVehicles(warehouse, 42.0) }
    }
}
