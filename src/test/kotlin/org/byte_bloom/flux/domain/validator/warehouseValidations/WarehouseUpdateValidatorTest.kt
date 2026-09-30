package org.byte_bloom.flux.domain.validator.warehouseValidations

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.request.WarehouseUpdateRequest
import org.byte_bloom.flux.domain.validation.EntityField
import org.byte_bloom.flux.domain.validation.ValidationField
import org.byte_bloom.flux.domain.validation.ValidationResult
import org.junit.jupiter.api.Test

class WarehouseUpdateValidatorTest {

    private val validator = WarehouseUpdateValidator()

    private fun request(
        name: String? = "Updated Warehouse",
        regionalZone: String? = "NORTH",
        latitude: Double? = 32.0,
        longitude: Double? = 35.0
    ) = WarehouseUpdateRequest(name = name, regionalZone = regionalZone, latitude = latitude, longitude = longitude)

    @Test
    fun `given valid update request when validate then returns Valid`() {
        // Given
        val updateRequest = request()

        // When
        val result = validator(updateRequest)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `given blank name when validate then returns Blank error`() {
        // Given
        val updateRequest = request(name = " ")

        // When
        val result = validator(updateRequest)

        // Then
        val errors = (result as ValidationResult.Invalid).errors

        assertThat(errors.map { it::class })
            .containsExactly(ValidationField.Blank::class)

        assertThat(errors.first().field)
            .isEqualTo(EntityField.WAREHOUSE_NAME)
    }

    @Test
    fun `given blank regional zone when validate then returns Blank error`() {
        // Given
        val updateRequest = request(regionalZone = " ")

        // When
        val result = validator(updateRequest)

        // Then
        val errors = (result as ValidationResult.Invalid).errors

        assertThat(errors.map { it::class })
            .containsExactly(ValidationField.Blank::class)

        assertThat(errors.first().field)
            .isEqualTo(EntityField.REGIONAL_ZONE)
    }

    @Test
    fun `given latitude below minimum when validate then returns OutOfRange error`() {
        // Given
        val updateRequest = request(latitude = -90.1)

        // When
        val result = validator(updateRequest)

        // Then
        val errors = (result as ValidationResult.Invalid).errors

        assertThat(errors.map { it::class })
            .containsExactly(ValidationField.OutOfRange::class)
    }

    @Test
    fun `given latitude above maximum when validate then returns OutOfRange error`() {
        // Given
        val updateRequest = request(latitude = 90.1)

        // When
        val result = validator(updateRequest)

        // Then
        val errors = (result as ValidationResult.Invalid).errors

        assertThat(errors.map { it::class })
            .containsExactly(ValidationField.OutOfRange::class)
    }

    @Test
    fun `given longitude below minimum when validate then returns OutOfRange error`() {
        // Given
        val updateRequest = request(longitude = -180.1)

        // When
        val result = validator(updateRequest)

        // Then
        val errors = (result as ValidationResult.Invalid).errors

        assertThat(errors.map { it::class })
            .containsExactly(ValidationField.OutOfRange::class)
    }

    @Test
    fun `given longitude above maximum when validate then returns OutOfRange error`() {
        // Given
        val updateRequest = request(longitude = 180.1)

        // When
        val result = validator(updateRequest)

        // Then
        val errors = (result as ValidationResult.Invalid).errors

        assertThat(errors.map { it::class })
            .containsExactly(ValidationField.OutOfRange::class)
    }

    @Test
    fun `given latitude and longitude at boundaries when validate then returns Valid`() {
        // Given
        val updateRequest = request(
            latitude = 90.0,
            longitude = 180.0
        )

        // When
        val result = validator(updateRequest)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `given no fields to update when validate then returns NoFieldUpdated`() {
        // Given
        val updateRequest = request(
            name = null,
            regionalZone = null,
            latitude = null,
            longitude = null
        )

        // When
        val result = validator(updateRequest)

        // Then
        val errors = (result as ValidationResult.Invalid).errors

        assertThat(errors.map { it::class })
            .containsExactly(ValidationField.NoFieldUpdated::class)
    }

    @Test
    fun `given blank name and invalid coordinates when validate then returns all errors`() {
        // Given
        val updateRequest = request(name = " ", latitude = 100.0, longitude = 200.0)

        // When
        val result = validator(updateRequest)

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