package org.byte_bloom.flux.domain.usecase.crud.vehicle

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.validator.vehicleValidation.VehicleCreateValidator


class CreateVehicleUseCaseTest {

    val repository: VehicleRepository = mockk()
    val useCase = CreateVehicleUseCase(repository, VehicleCreateValidator())

    @Test
    fun `creates the vehicle through the repository when it is valid`() = runBlocking<Unit> {
        // Given
        val vehicle = aVehicle()
        coEvery { repository.create(vehicle) } returns Result.success(vehicle)

        // When
        val result = useCase(vehicle)

        // Then
        assertThat(result.getOrNull()).isEqualTo(vehicle)
        coVerify(exactly = 1) { repository.create(vehicle) }
    }

    @Test
    fun `fails with EntityValidationException and never touches the repository when vehicle is invalid`() =runBlocking<Unit> {
        // Given
        val vehicle = aVehicle(capacity = -1.0)

        // When
        val result = useCase(vehicle)

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(LogisticsException.ValidationException.EntityValidationException::class.java)
        coVerify { repository wasNot Called }

    }

    @Test
    fun `propagates the repository failure unchanged`() = runBlocking<Unit> {
        // Given
        val vehicle = aVehicle()
        val failure = LogisticsException.DataAccessException.NetworkUnavailableException()
        coEvery { repository.create(vehicle) } returns Result.failure(failure)

        // When
        val result = useCase(vehicle)

        // Then
        assertThat(result.exceptionOrNull()).isEqualTo(failure)
    }

    @Test
    fun `failure message names every invalid field`() = runBlocking<Unit> {
        // Given
        val invalid = aVehicle(hub = aWarehouse(id = ""), capacity = 0.0)

        // When
        val error = useCase(invalid).exceptionOrNull()

        // Then
        assertThat(error!!.message).contains("CURRENT_HUB_ID")
        assertThat(error.message).contains("MAX_CAPACITY_KG")
    }

    fun aWarehouse(
        id: String = "WH-001",
        name: String = "Test Hub",
        zone: RegionalZone = RegionalZone.NORTH
    ) = Warehouse(id, name, zone, 0.0, 0.0)

    fun aVehicle(
        id: String = "V-001",
        hub: Warehouse = aWarehouse(),
        capacity: Double = 500.0,
        costPerKm: Double = 2.0
    ) = Vehicle(id, hub, capacity, costPerKm)
}
