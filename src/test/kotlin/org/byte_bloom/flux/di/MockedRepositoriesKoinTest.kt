package org.byte_bloom.flux.di

import com.google.common.truth.ExpectFailure.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.RouteRepository
import org.byte_bloom.flux.domain.repository.VehicleRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.testdata.aVehicle
import org.byte_bloom.flux.domain.usecase.*
import org.byte_bloom.flux.domain.usecase.crud.pakage.*
import org.byte_bloom.flux.domain.usecase.crud.route.*
import org.byte_bloom.flux.domain.usecase.crud.vehicle.*
import org.byte_bloom.flux.domain.usecase.crud.warehouse.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

val mockRepositoriesModule = module {
    single<WarehouseRepository> { mockk() }
    single<PackageRepository> { mockk() }
    single<VehicleRepository> { mockk() }
    single<RouteRepository> { mockk() }
}

class MockedRepositoriesKoinTest {

    @AfterEach
    fun tearDown() = stopKoin()

    @Test
    fun `use cases resolve against mocked repositories without touching the network`() {
        // Given: no networkModule and no repositoryModule are loaded
        startKoin { modules(mockRepositoriesModule, validatorModule, useCaseModule) }

        // When
        val createVehicle: CreateVehicleUseCase = getKoin().get()
        val repository: VehicleRepository = getKoin().get()
        coEvery { repository.create(any()) } returns Result.success(aVehicle())

        // Then
        val result = runBlocking { createVehicle(aVehicle()) }
        assertTrue(result.isSuccess)
    }
}
