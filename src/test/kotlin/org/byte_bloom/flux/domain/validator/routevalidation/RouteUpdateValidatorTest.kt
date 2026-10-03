package org.byte_bloom.flux.domain.validator.routevalidation

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.request.RouteUpdateRequest
import org.byte_bloom.flux.domain.validation.EntityField
import org.byte_bloom.flux.domain.validation.ValidationField
import org.byte_bloom.flux.domain.validation.ValidationResult
import org.junit.jupiter.api.Test

class RouteUpdateValidatorTest {

    private val validator = RouteUpdateValidator()

    private fun request(
        originHubId: String? = null,
        destinationHubId: String? = null,
        distanceKm: Double? = null,
        typicalDelayMin: Double? = null
    ) = RouteUpdateRequest(originHubId, destinationHubId, distanceKm, typicalDelayMin)

    private fun errorsOf(result: ValidationResult) = (result as ValidationResult.Invalid).errors

    @Test
    fun `given only distance when validate then returns Valid`() {
        // Given
        val updateRequest = request(distanceKm = 80.0)

        // When
        val result = validator(updateRequest)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `given only origin hub id when validate then returns Valid`() {
        // Given
        val updateRequest = request(originHubId = "WH-5")

        // When
        val result = validator(updateRequest)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `given zero typical delay when validate then returns Valid`() {
        // Given
        val updateRequest = request(typicalDelayMin = 0.0)

        // When
        val result = validator(updateRequest)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `given all fields valid when validate then returns Valid`() {
        // Given
        val updateRequest = request(
            originHubId = "WH-3",
            destinationHubId = "WH-4",
            distanceKm = 60.0,
            typicalDelayMin = 10.0
        )

        // When
        val result = validator(updateRequest)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `given zero distance when validate then returns NotPositive error`() {
        // Given
        val updateRequest = request(distanceKm = 0.0)

        // When
        val result = validator(updateRequest)

        // Then
        val errors = errorsOf(result)
        assertThat(errors.map { it::class }).containsExactly(ValidationField.NotPositive::class)
        assertThat(errors.first().field).isEqualTo(EntityField.DISTANCE_KM)
    }

    @Test
    fun `given negative distance when validate then returns NotPositive error`() {
        // Given
        val updateRequest = request(distanceKm = -10.0)

        // When
        val result = validator(updateRequest)

        // Then
        val errors = errorsOf(result)
        assertThat(errors.map { it::class }).containsExactly(ValidationField.NotPositive::class)
        assertThat(errors.first().field).isEqualTo(EntityField.DISTANCE_KM)
    }

    @Test
    fun `given negative typical delay when validate then returns Negative error`() {
        // Given
        val updateRequest = request(typicalDelayMin = -3.0)

        // When
        val result = validator(updateRequest)

        // Then
        val error = errorsOf(result).single() as ValidationField.Negative
        assertThat(error.field).isEqualTo(EntityField.TYPICAL_DELAY_MIN)
        assertThat(error.actualValue).isEqualTo(-3.0)
    }

    @Test
    fun `given no fields to update when validate then returns NoFieldUpdated`() {
        // Given
        val updateRequest = request()

        // When
        val result = validator(updateRequest)

        // Then
        assertThat(errorsOf(result).map { it::class }).containsExactly(ValidationField.NoFieldUpdated::class)
    }

    @Test
    fun `given blank hub ids and no numbers when validate then returns NoFieldUpdated`() {
        // Given
        val updateRequest = request(originHubId = "", destinationHubId = " ")

        // When
        val result = validator(updateRequest)

        // Then
        assertThat(errorsOf(result).map { it::class }).containsExactly(ValidationField.NoFieldUpdated::class)
    }

    @Test
    fun `given invalid distance and delay when validate then returns all errors`() {
        // Given
        val updateRequest = request(distanceKm = 0.0, typicalDelayMin = -1.0)

        // When
        val result = validator(updateRequest)

        // Then
        assertThat(errorsOf(result).map { it.field })
            .containsExactly(EntityField.DISTANCE_KM, EntityField.TYPICAL_DELAY_MIN)
            .inOrder()
    }
}
