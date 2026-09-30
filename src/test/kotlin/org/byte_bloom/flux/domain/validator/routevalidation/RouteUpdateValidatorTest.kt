package org.byte_bloom.flux.domain.validator.routevalidation

import org.byte_bloom.flux.domain.request.RouteUpdateRequest
import org.byte_bloom.flux.domain.validation.EntityField
import org.byte_bloom.flux.domain.validation.ValidationField
import org.byte_bloom.flux.domain.validation.ValidationResult
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RouteUpdateValidatorTest {

    private val validator = RouteUpdateValidator()

    @Test
    fun `returns Valid when only distance is updated`() {
        // Given
        val request = anUpdateRequest(distanceKm = 80.0)

        // When
        val result = validator(request)

        // Then
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `returns Valid when only origin hub id is updated`() {
        // Given
        val request = anUpdateRequest(originHubId = "WH-005")

        // When
        val result = validator(request)

        // Then
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `returns Valid when typical delay is updated to zero`() {
        // Given
        val request = anUpdateRequest(typicalDelayMin = 0.0)

        // When
        val result = validator(request)

        // Then
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `returns Valid when all fields are updated with valid values`() {
        // Given
        val request = anUpdateRequest(
            originHubId = "WH-003",
            destinationHubId = "WH-004",
            distanceKm = 60.0,
            typicalDelayMin = 10.0
        )

        // When
        val result = validator(request)

        // Then
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `returns NotPositive error on DISTANCE_KM when distance is zero`() {
        // Given
        val request = anUpdateRequest(distanceKm = 0.0)

        // When
        val result = validator(request)

        // Then
        val error = errorsOf(result).single()
        assertIs<ValidationField.NotPositive>(error)
        assertEquals(EntityField.DISTANCE_KM, error.field)
        assertEquals(0.0, error.actualValue)
    }

    @Test
    fun `returns NotPositive error on DISTANCE_KM when distance is negative`() {
        // Given
        val request = anUpdateRequest(distanceKm = -10.0)

        // When
        val result = validator(request)

        // Then
        val error = errorsOf(result).single()
        assertIs<ValidationField.NotPositive>(error)
        assertEquals(EntityField.DISTANCE_KM, error.field)
    }

    @Test
    fun `returns Negative error on TYPICAL_DELAY_MIN when delay is negative`() {
        // Given
        val request = anUpdateRequest(typicalDelayMin = -3.0)

        // When
        val result = validator(request)

        // Then
        val error = errorsOf(result).single()
        assertIs<ValidationField.Negative>(error)
        assertEquals(EntityField.TYPICAL_DELAY_MIN, error.field)
        assertEquals(-3.0, error.actualValue)
    }

    @Test
    fun `returns NoFieldUpdated error when all fields are null`() {
        // Given
        val request = anUpdateRequest()

        // When
        val result = validator(request)

        // Then
        val error = errorsOf(result).single()
        assertIs<ValidationField.NoFieldUpdated>(error)
        assertEquals(EntityField.NONE, error.field)
    }

    @Test
    fun `returns NoFieldUpdated error when hub ids are blank and numbers are null`() {
        // Given
        val request = anUpdateRequest(originHubId = "", destinationHubId = "   ")

        // When
        val result = validator(request)

        // Then
        assertIs<ValidationField.NoFieldUpdated>(errorsOf(result).single())
    }

    @Test
    fun `returns both errors when distance and delay are invalid`() {
        // Given
        val request = anUpdateRequest(distanceKm = 0.0, typicalDelayMin = -1.0)

        // When
        val result = validator(request)

        // Then
        assertEquals(
            listOf(EntityField.DISTANCE_KM, EntityField.TYPICAL_DELAY_MIN),
            errorsOf(result).map { it.field }
        )
    }

    private fun anUpdateRequest(
        originHubId: String? = null,
        destinationHubId: String? = null,
        distanceKm: Double? = null,
        typicalDelayMin: Double? = null
    ) = RouteUpdateRequest(
        originHubId = originHubId,
        destinationHubId = destinationHubId,
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin
    )

    private fun errorsOf(result: ValidationResult): List<ValidationField> =
        assertIs<ValidationResult.Invalid>(result).errors
}
