package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.assertThrows

class GetWarehouseLoadFactorUseCaseTest {

    private val useCase = GetWarehouseLoadFactorUseCase()

    private fun warehouse(id: String = "W1") =
        Warehouse(
            id = id,
            name = id,
            regionalZone = RegionalZone.CENTRAL,
            latitude = 0.0,
            longitude = 0.0
        )

    private fun vehicle(
        id: String,
        warehouse: Warehouse,
        capacity: Double
    ) =
        Vehicle(
            id = id,
            currentHub = warehouse,
            maxCapacityKg = capacity,
            costPerKm = 1.0
        )

    private fun packageItem(
        id: String,
        warehouse: Warehouse,
        weight: Double?
    ) =
        Package(
            id = id,
            weight = weight,
            originHub = warehouse,
            destinationHub = warehouse,
            priority = Priority.STANDARD
        )

    @Test
    fun `should calculate warehouse load factor`() {
        // Given
        val warehouse = warehouse()

        warehouse.addVehicle(
            vehicle("V1", warehouse, 100.0)
        )

        warehouse.addPackage(
            packageItem("P1", warehouse, 40.0)
        )

        // When
        val result = useCase(warehouse)

        // Then
        assertEquals(0.4, result, 0.001)
    }

    @Test
    fun `should sum capacities of multiple stationed vehicles`() {
        // Given
        val warehouse = warehouse()

        warehouse.addVehicle(vehicle("V1", warehouse, 100.0))

        warehouse.addVehicle(vehicle("V2", warehouse, 100.0))

        warehouse.addPackage(packageItem("P1", warehouse, 50.0))

        // When
        val result = useCase(warehouse)

        // Then
        assertEquals(0.25, result, 0.001)
    }

    @Test
    fun `should return zero when cargo queue is empty`() {
        // Given
        val warehouse = warehouse()

        warehouse.addVehicle(vehicle("V1", warehouse, 100.0))

        // When
        val result = useCase(warehouse)

        // Then
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `should treat null package weight as zero`() {
        // Given
        val warehouse = warehouse()

        warehouse.addVehicle(vehicle("V1", warehouse, 100.0))

        warehouse.addPackage(packageItem("P1", warehouse, null))

        // When
        val result = useCase(warehouse)

        // Then
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `should return one when queue weight equals fleet capacity`() {
        // Given
        val warehouse = warehouse()

        warehouse.addVehicle(vehicle("V1", warehouse, 100.0))

        warehouse.addPackage(packageItem("P1", warehouse, 100.0))

        // When
        val result = useCase(warehouse)

        // Then
        assertEquals(1.0, result, 0.001)
    }

    @Test
    fun `should throw exception when warehouse has no stationed vehicles`() {
        // Given
        val warehouse = warehouse()

        // When & Then
        assertThrows<LogisticsException.BusinessLogicException.NoStationedVehiclesException> {
            useCase(warehouse)
        }
    }
}