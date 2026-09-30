package org.byte_bloom.flux.domain.validator.packagevalidations

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.request.PackageUpdateRequest
import org.byte_bloom.flux.domain.validation.EntityField
import org.byte_bloom.flux.domain.validation.ValidationField
import org.byte_bloom.flux.domain.validation.ValidationResult
import org.junit.jupiter.api.Test

class PackageUpdateValidatorTest {

    private val validator = PackageUpdateValidator()

    private fun request(
        weight: Double? = null,
        originHubId: String? = null,
        destinationHubId: String? = null,
        priority: String? = null
    ) = PackageUpdateRequest(weight, originHubId, destinationHubId, priority)

    @Test
    fun `given only weight provided when validate then returns Valid`() {
        // Given
        val request = request(weight = 12.5)

        // When
        val result = validator(request)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `given only priority provided when validate then returns Valid`() {
        // Given
        val request = request(priority = "URGENT")

        // When
        val result = validator(request)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `given all fields null when validate then returns NoFieldUpdated error`() {
        // Given
        val request = request()

        // When
        val result = validator(request)

        // Then
        val errors = (result as ValidationResult.Invalid).errors
        assertThat(errors.map { it::class }).containsExactly(ValidationField.NoFieldUpdated::class)
    }

    @Test
    fun `given all fields blank or null when validate then returns NoFieldUpdated error`() {
        // Given
        val request = request(originHubId = "", destinationHubId = "  ", priority = " ")

        // When
        val result = validator(request)

        // Then
        val errors = (result as ValidationResult.Invalid).errors
        assertThat(errors.map { it::class }).containsExactly(ValidationField.NoFieldUpdated::class)
    }

    @Test
    fun `given negative weight when validate then returns Negative error on WEIGHT`() {
        // Given
        val request = request(weight = -3.0)

        // When
        val result = validator(request)

        // Then
        val errors = (result as ValidationResult.Invalid).errors
        assertThat(errors.map { it::class }).containsExactly(ValidationField.Negative::class)
        assertThat(errors.single().field).isEqualTo(EntityField.WEIGHT)
    }

    @Test
    fun `given zero weight when validate then returns Valid (differs from create validator)`() {
        // Given
        val request = request(weight = 0.0)

        // When
        val result = validator(request)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }
}