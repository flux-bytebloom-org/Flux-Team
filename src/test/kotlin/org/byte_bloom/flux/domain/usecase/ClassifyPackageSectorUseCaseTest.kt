package org.byte_bloom.flux.domain.usecase

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.CargoQuery
import org.byte_bloom.flux.domain.model.CargoRecord
import org.byte_bloom.flux.domain.model.RegionalZone
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ClassifyPackageSectorUseCaseTest {

    private val useCase = ClassifyPackageSectorUseCase()

    // Longitude and weight are constant in these helpers, so only latitude decides the distance.
    private fun record(latitude: Double, sector: RegionalZone) =
        CargoRecord(latitude = latitude, longitude = 35.0, weightKg = 10.0, sector = sector)

    private fun query(latitude: Double) =
        CargoQuery(latitude = latitude, longitude = 35.0, weightKg = 10.0)

    private val twoClusters = listOf(
        record(0.0, RegionalZone.NORTH), record(1.0, RegionalZone.NORTH),
        record(2.0, RegionalZone.NORTH), record(3.0, RegionalZone.NORTH),
        record(4.0, RegionalZone.NORTH),
        record(20.0, RegionalZone.SOUTH), record(21.0, RegionalZone.SOUTH),
        record(22.0, RegionalZone.SOUTH), record(23.0, RegionalZone.SOUTH),
        record(24.0, RegionalZone.SOUTH)
    )

    @Test
    fun `given a query near the NORTH records when classify then returns NORTH`() {
        // When
        val result = useCase(query(1.0), twoClusters)

        // Then
        assertThat(result).isEqualTo(RegionalZone.NORTH)
    }

    @Test
    fun `given a query near the SOUTH records when classify then returns SOUTH`() {
        // When
        val result = useCase(query(22.0), twoClusters)

        // Then
        assertThat(result).isEqualTo(RegionalZone.SOUTH)
    }

    @Test
    fun `given more records than k when classify then only the 5 nearest vote`() {
        // Given: 3 NORTH are close, 4 SOUTH are a bit farther. All 7 voting would give SOUTH.
        val records = listOf(
            record(0.0, RegionalZone.NORTH), record(1.0, RegionalZone.NORTH),
            record(2.0, RegionalZone.NORTH),
            record(3.0, RegionalZone.SOUTH), record(4.0, RegionalZone.SOUTH),
            record(5.0, RegionalZone.SOUTH), record(6.0, RegionalZone.SOUTH)
        )

        // When
        val result = useCase(query(0.0), records)

        // Then
        assertThat(result).isEqualTo(RegionalZone.NORTH)
    }

    @Test
    fun `given a custom k of 1 when classify then only the nearest record decides`() {
        // Given
        val records = listOf(
            record(0.0, RegionalZone.SOUTH),
            record(1.0, RegionalZone.NORTH), record(2.0, RegionalZone.NORTH),
            record(3.0, RegionalZone.NORTH), record(4.0, RegionalZone.NORTH)
        )

        // When
        val withK1 = ClassifyPackageSectorUseCase(k = 1)(query(0.0), records)
        val withDefaultK = useCase(query(0.0), records)

        // Then
        assertThat(withK1).isEqualTo(RegionalZone.SOUTH)
        assertThat(withDefaultK).isEqualTo(RegionalZone.NORTH)
    }

    @Test
    fun `given fewer records than k when classify then uses all of them`() {
        // Given
        val records = listOf(
            record(0.0, RegionalZone.NORTH), record(1.0, RegionalZone.NORTH),
            record(2.0, RegionalZone.SOUTH)
        )

        // When
        val result = useCase(query(0.0), records)

        // Then
        assertThat(result).isEqualTo(RegionalZone.NORTH)
    }

    @Test
    fun `given a tie on votes when classify then the sector with the nearest record wins`() {
        // Given
        val records = listOf(
            record(0.0, RegionalZone.SOUTH),
            record(1.0, RegionalZone.NORTH)
        )

        // When
        val result = useCase(query(0.2), records)

        // Then
        assertThat(result).isEqualTo(RegionalZone.SOUTH)
    }

    @Test
    fun `given features on different scales when classify then normalization keeps coordinates relevant`() {
        // Given: SOUTH is almost at the same coordinates, but its weight is far from the query
        // Without normalization the weight difference would dominate and the answer would be NORTH
        val records = listOf(
            CargoRecord(latitude = 31.0, longitude = 34.0, weightKg = 10.0, sector = RegionalZone.SOUTH),
            CargoRecord(latitude = 33.0, longitude = 36.0, weightKg = 90.0, sector = RegionalZone.NORTH)
        )
        val newPackage = CargoQuery(latitude = 31.1, longitude = 34.1, weightKg = 60.0)

        // When
        val result = useCase(newPackage, records)

        // Then
        assertThat(result).isEqualTo(RegionalZone.SOUTH)
    }

    @Test
    fun `given a query outside the training range when classify then still returns the closest sector`() {
        // Given
        val records = listOf(
            record(0.0, RegionalZone.NORTH),
            record(10.0, RegionalZone.SOUTH)
        )

        // When
        val result = useCase(query(-5.0), records)

        // Then
        assertThat(result).isEqualTo(RegionalZone.NORTH)
    }

    @Test
    fun `given all records at the same place when classify then the majority sector wins without crashing`() {
        // Given
        val records = listOf(
            record(32.0, RegionalZone.NORTH),
            record(32.0, RegionalZone.NORTH),
            record(32.0, RegionalZone.SOUTH)
        )

        // When
        val result = useCase(query(40.0), records)

        // Then
        assertThat(result).isEqualTo(RegionalZone.NORTH)
    }

    @Test
    fun `given no training records when classify then throws InsufficientTrainingDataException`() {
        assertFailsWith<LogisticsException.BusinessLogicException.InsufficientTrainingDataException> {
            useCase(query(1.0), emptyList())
        }
    }

    @Test
    fun `given k equal to 0 when creating the use case then throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            ClassifyPackageSectorUseCase(k = 0)
        }
    }
}