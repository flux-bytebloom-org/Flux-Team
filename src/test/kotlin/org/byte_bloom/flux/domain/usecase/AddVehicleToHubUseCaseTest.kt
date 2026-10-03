package org.byte_bloom.flux.domain.usecase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.testdata.createTestVehicle
import org.byte_bloom.flux.domain.testdata.createTestWarehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class AddVehicleToHubUseCaseTest {

    private val vehicleRepo: VehicleRepository = mockk()
    private val useCase = AddVehicleToHubUseCase(vehicleRepo)

    private val oldHub = createTestWarehouse("WH-1")
    private val newHub = createTestWarehouse("WH-2")
    private val vehicle = createTestVehicle(hub = oldHub)

    @Test
    fun `given vehicle and hub when add then returns the vehicle updated by repository`() {
        // Given
        val movedVehicle = vehicle.copy(currentHub = newHub)
        every { vehicleRepo.updateVehicleCurrentHub(vehicle, newHub) } returns movedVehicle

        // When
        val result = useCase(newHub, vehicle)

        // Then
        assertThat(result).isEqualTo(movedVehicle)
        assertThat(result.currentHub).isEqualTo(newHub)
    }

    @Test
    fun `given vehicle and hub when add then calls repository once with same arguments`() {
        // Given
        every { vehicleRepo.updateVehicleCurrentHub(any(), any()) } returns vehicle.copy(currentHub = newHub)

        // When
        useCase(newHub, vehicle)

        // Then
        verify(exactly = 1) { vehicleRepo.updateVehicleCurrentHub(vehicle, newHub) }
    }

    @Test
    fun `given repository throws when add then exception propagates`() {
        // Given
        every { vehicleRepo.updateVehicleCurrentHub(vehicle, newHub) } throws
                LogisticsException.EntityNotFoundException.VehicleNotFoundException(vehicle.id)

        // When / Then
        assertFailsWith<LogisticsException.EntityNotFoundException.VehicleNotFoundException> {
            useCase(newHub, vehicle)
        }
    }
}