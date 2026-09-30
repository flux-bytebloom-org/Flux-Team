package org.byte_bloom.flux.domain.validator.warehouseValidations

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.validation.ValidationField
import org.byte_bloom.flux.domain.validation.ValidationResult
import org.junit.jupiter.api.Test

class WarehouseCreateValidatorTest {

    private val validator = WarehouseCreateValidator()

    private fun warehouse(
        name: String = "Main Warehouse",
        latitude: Double = 32.0,
        longitude: Double = 35.0
    ) = Warehouse(id = "WH-1", name = name, regionalZone = RegionalZone.CENTRAL, latitude = latitude, longitude = longitude)

    @Test
    fun `given valid warehouse when validate then returns Valid`() {
        // Given
        val warehouse = warehouse()

        // When
        val result = validator(warehouse)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `given blank name when validate then returns Blank error`() {
        // Given
        val warehouse = warehouse(name = "   ")

        // When
        val result = validator(warehouse)

        // Then
        val errors = (result as ValidationResult.Invalid).errors

        assertThat(errors.map { it::class })
            .containsExactly(ValidationField.Blank::class)

        assertThat(errors.first().field)
            .isEqualTo(org.byte_bloom.flux.domain.validation.EntityField.WAREHOUSE_NAME)
    }

    @Test
    fun `given latitude below minimum when validate then returns OutOfRange error`() {
        // Given
        val warehouse = warehouse(latitude = -90.1)

        // When
        val result = validator(warehouse)

        // Then
        val errors = (result as ValidationResult.Invalid).errors

        assertThat(errors.map { it::class })
            .containsExactly(ValidationField.OutOfRange::class)
    }

    @Test
    fun `given latitude above maximum when validate then returns OutOfRange error`() {
        // Given
        val warehouse = warehouse(latitude = 90.1)

        // When
        val result = validator(warehouse)

        // Then
        val errors = (result as ValidationResult.Invalid).errors

        assertThat(errors.map { it::class })
            .containsExactly(ValidationField.OutOfRange::class)
    }

    @Test
    fun `given longitude below minimum when validate then returns OutOfRange error`() {
        // Given
        val warehouse = warehouse(longitude = -180.1)

        // When
        val result = validator(warehouse)

        // Then
        val errors = (result as ValidationResult.Invalid).errors

        assertThat(errors.map { it::class })
            .containsExactly(ValidationField.OutOfRange::class)
    }

    @Test
    fun `given longitude above maximum when validate then returns OutOfRange error`() {
        // Given
        val warehouse = warehouse(longitude = 180.1)

        // When
        val result = validator(warehouse)

        // Then
        val errors = (result as ValidationResult.Invalid).errors

        assertThat(errors.map { it::class })
            .containsExactly(ValidationField.OutOfRange::class)
    }

    @Test
    fun `given latitude and longitude at boundaries when validate then returns Valid`() {
        // Given
        val warehouse = warehouse(latitude = 90.0, longitude = 180.0)

        // When
        val result = validator(warehouse)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `given multiple invalid fields when validate then returns all errors`() {
        // Given
        val warehouse = warehouse(name = " ", latitude = 100.0, longitude = 200.0)

        // When
        val result = validator(warehouse)

        // Then
        val errors = (result as ValidationResult.Invalid).errors

        assertThat(errors.map { it::class })
            .containsExactly(
                ValidationField.Blank::class,
                ValidationField.OutOfRange::class,
                ValidationField.OutOfRange::class
            )
    }
}