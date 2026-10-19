package org.byte_bloom.flux.di

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.model.CargoQuery
import org.byte_bloom.flux.domain.model.CargoRecord
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.usecase.ClassifyPackageSectorUseCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.java.KoinJavaComponent.getKoin

class ClassifyPackageSectorKoinTest {

    @AfterEach
    fun tearDown() = stopKoin()

    private fun record(latitude: Double, sector: RegionalZone) =
        CargoRecord(latitude = latitude, longitude = 35.0, weightKg = 10.0, sector = sector)

    @Test
    fun `given only the use case module when resolving the KNN use case then it classifies with the default k`() {
        // Given: no repositories are needed for this use case
        startKoin { modules(useCaseModule) }
        val useCase: ClassifyPackageSectorUseCase = getKoin().get()
        val records = listOf(
            record(0.0, RegionalZone.NORTH), record(1.0, RegionalZone.NORTH),
            record(2.0, RegionalZone.NORTH),
            record(3.0, RegionalZone.SOUTH), record(4.0, RegionalZone.SOUTH),
            record(5.0, RegionalZone.SOUTH), record(6.0, RegionalZone.SOUTH)
        )

        // When
        val result = useCase(CargoQuery(latitude = 0.0, longitude = 35.0, weightKg = 10.0), records)

        // Then: with k = 5 the 3 NORTH records win, with all 7 voting SOUTH would win
        assertThat(result).isEqualTo(RegionalZone.NORTH)
    }

    @Test
    fun `given the use case module when resolving twice then each call returns a new instance`() {
        // Given
        startKoin { modules(useCaseModule) }

        // When
        val first: ClassifyPackageSectorUseCase = getKoin().get()
        val second: ClassifyPackageSectorUseCase = getKoin().get()

        // Then
        assertThat(first).isNotSameInstanceAs(second)
    }
}