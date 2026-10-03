package org.byte_bloom.flux.domain.usecase

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.testdata.createTestVehicle
import org.byte_bloom.flux.domain.testdata.createTestWarehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class FindStationedVehiclesByCapacityUseCaseTest {

    private val useCase = FindStationedVehiclesByCapacityUseCase()

    @Test
    fun `given vehicles with different capacities when find then returns only vehicles that can carry the weight`() {
        // Given
        val warehouse = createTestWarehouse()
        val smallVehicle = createTestVehicle(id = "V-1", maxCapacityKg = 50.0)
        val largeVehicle = createTestVehicle(id = "V-2", maxCapacityKg = 200.0)
        warehouse.addVehicle(smallVehicle)
        warehouse.addVehicle(largeVehicle)

        // When
        val result = useCase(warehouse, 100.0)

        // Then
        assertThat(result).containsExactly(largeVehicle)
    }

    @Test
    fun `given vehicle capacity equal to required weight when find then vehicle is included`() {
        // Given
        val warehouse = createTestWarehouse()
        val exactVehicle = createTestVehicle(maxCapacityKg = 100.0)
        warehouse.addVehicle(exactVehicle)

        // When
        val result = useCase(warehouse, 100.0)

        // Then
        assertThat(result).containsExactly(exactVehicle)
    }

    @Test
    fun `given no vehicle can carry the weight when find then returns empty list`() {
        // Given
        val warehouse = createTestWarehouse()
        warehouse.addVehicle(createTestVehicle(maxCapacityKg = 50.0))

        // When
        val result = useCase(warehouse, 100.0)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `given warehouse without vehicles when find then returns empty list`() {
        // Given
        val warehouse = createTestWarehouse()

        // When
        val result = useCase(warehouse, 10.0)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `given negative required weight when find then throws InvalidRequiredWeightException`() {
        // Given
        val warehouse = createTestWarehouse()

        // When / Then
        assertFailsWith<LogisticsException.ValidationException.InvalidRequiredWeightException> {
            useCase(warehouse, -1.0)
        }
    }
}