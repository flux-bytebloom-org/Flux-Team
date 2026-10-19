package org.byte_bloom.flux.domain.mapper

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.model.CargoQuery
import org.byte_bloom.flux.domain.model.CargoRecord
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.testdata.createTestPackage
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CargoMapperTest {

    private fun warehouse(
        id: String = "WH-1",
        zone: RegionalZone = RegionalZone.NORTH,
        latitude: Double = 32.2,
        longitude: Double = 35.2
    ) = Warehouse(id, "Test Hub", zone, latitude, longitude)

    private fun pkg(
        id: String = "PKG-1",
        weight: Double? = 25.0,
        destination: Warehouse = warehouse()
    ) = createTestPackage(id = id, weight = weight, destination = destination)

    // ===== toCargoQuery =====

    @Test
    fun `given a valid package when toCargoQuery then uses destination coordinates and weight`() {
        // When
        val result = pkg().toCargoQuery()

        // Then
        assertThat(result).isEqualTo(CargoQuery(latitude = 32.2, longitude = 35.2, weightKg = 25.0))
    }

    @Test
    fun `given different origin and destination when toCargoQuery then uses the destination hub`() {
        // Given
        val origin = warehouse(id = "WH-9", zone = RegionalZone.SOUTH, latitude = 10.0, longitude = 10.0)
        val pkg = createTestPackage(weight = 25.0, origin = origin, destination = warehouse())

        // When
        val result = pkg.toCargoQuery()

        // Then
        assertThat(result).isEqualTo(CargoQuery(latitude = 32.2, longitude = 35.2, weightKg = 25.0))
    }

    @Test
    fun `given a package without weight when toCargoQuery then returns null`() {
        // When
        val result = pkg(weight = null).toCargoQuery()

        // Then
        assertThat(result).isNull()
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, -3.0, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY])
    fun `given an invalid weight when toCargoQuery then returns null`(invalidWeight: Double) {
        // When
        val result = pkg(weight = invalidWeight).toCargoQuery()

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `given a tiny positive weight when toCargoQuery then returns a query`() {
        // When
        val result = pkg(weight = 0.1).toCargoQuery()

        // Then
        assertThat(result?.weightKg).isEqualTo(0.1)
    }

    @Test
    fun `given a destination with unknown zone when toCargoQuery then still returns a query`() {
        // Given: the zone is exactly what we want to predict, so it must not block the query
        val pkg = pkg(destination = warehouse(zone = RegionalZone.UnKNOWN))

        // When
        val result = pkg.toCargoQuery()

        // Then
        assertThat(result).isNotNull()
    }

    // ===== toCargoRecord =====

    @Test
    fun `given a valid package when toCargoRecord then builds a record with the destination sector`() {
        // When
        val result = pkg().toCargoRecord()

        // Then
        assertThat(result).isEqualTo(
            CargoRecord(latitude = 32.2, longitude = 35.2, weightKg = 25.0, sector = RegionalZone.NORTH)
        )
    }

    @Test
    fun `given a package without weight when toCargoRecord then returns null`() {
        // When
        val result = pkg(weight = null).toCargoRecord()

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `given a destination with unknown zone when toCargoRecord then returns null`() {
        // When
        val result = pkg(destination = warehouse(zone = RegionalZone.UnKNOWN)).toCargoRecord()

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `given a destination at coordinates 0 and 0 when toCargoRecord then returns null`() {
        // When
        val result = pkg(destination = warehouse(latitude = 0.0, longitude = 0.0)).toCargoRecord()

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `given only the latitude equal to 0 when toCargoRecord then returns a record`() {
        // When
        val result = pkg(destination = warehouse(latitude = 0.0, longitude = 35.2)).toCargoRecord()

        // Then
        assertThat(result).isNotNull()
    }

    @Test
    fun `given only the longitude equal to 0 when toCargoRecord then returns a record`() {
        // When
        val result = pkg(destination = warehouse(latitude = 32.2, longitude = 0.0)).toCargoRecord()

        // Then
        assertThat(result).isNotNull()
    }

    // ===== toCargoRecords =====

    @Test
    fun `given a mixed list when toCargoRecords then converts only the valid packages in order`() {
        // Given
        val packages = listOf(
            pkg(id = "PKG-1", weight = 10.0),
            pkg(id = "PKG-2", weight = null),
            pkg(id = "PKG-3", destination = warehouse(zone = RegionalZone.UnKNOWN)),
            pkg(
                id = "PKG-4",
                weight = 40.0,
                destination = warehouse(id = "WH-2", zone = RegionalZone.SOUTH, latitude = 31.5, longitude = 34.8)
            ),
            pkg(id = "PKG-5", destination = warehouse(latitude = 0.0, longitude = 0.0))
        )

        // When
        val result = packages.toCargoRecords()

        // Then
        assertThat(result).containsExactly(
            CargoRecord(latitude = 32.2, longitude = 35.2, weightKg = 10.0, sector = RegionalZone.NORTH),
            CargoRecord(latitude = 31.5, longitude = 34.8, weightKg = 40.0, sector = RegionalZone.SOUTH)
        ).inOrder()
    }

    @Test
    fun `given an excluded id when toCargoRecords then that package is left out`() {
        // Given
        val packages = listOf(pkg(id = "PKG-1", weight = 10.0), pkg(id = "PKG-2", weight = 20.0))

        // When
        val result = packages.toCargoRecords(excludingId = "PKG-1")

        // Then
        assertThat(result).containsExactly(
            CargoRecord(latitude = 32.2, longitude = 35.2, weightKg = 20.0, sector = RegionalZone.NORTH)
        )
    }

    @Test
    fun `given an excluded id that does not exist when toCargoRecords then nothing is removed`() {
        // Given
        val packages = listOf(pkg(id = "PKG-1"), pkg(id = "PKG-2"))

        // When
        val result = packages.toCargoRecords(excludingId = "PKG-404")

        // Then
        assertThat(result).hasSize(2)
    }

    @Test
    fun `given an empty list when toCargoRecords then returns an empty list`() {
        // When
        val result = emptyList<org.byte_bloom.flux.domain.model.Package>().toCargoRecords()

        // Then
        assertThat(result).isEmpty()
    }
}