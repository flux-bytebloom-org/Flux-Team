package org.byte_bloom.flux.domain.validator.routevalidation

import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Route
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.validation.EntityField
import org.byte_bloom.flux.domain.validation.ValidationField
import org.byte_bloom.flux.domain.validation.ValidationResult
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RouteCreateValidatorTest {

    private val validator = RouteCreateValidator()

    @Test
    fun `returns Valid when all fields are valid`() {
        // Given
        val route = aValidRoute()

        // When
        val result = validator(route)

        // Then
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `returns Valid when typical delay is zero`() {
        // Given
        val route = aValidRoute(typicalDelayMin = 0.0)

        // When
        val result = validator(route)

        // Then
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `returns Blank error on ORIGIN_HUB_ID when origin hub id is empty`() {
        // Given
        val route = aValidRoute(originHubId = "")

        // When
        val result = validator(route)

        // Then
        val error = errorsOf(result).single()
        assertIs<ValidationField.Blank>(error)
        assertEquals(EntityField.ORIGIN_HUB_ID, error.field)
    }

    @Test
    fun `returns Blank error on ORIGIN_HUB_ID when origin hub id is only spaces`() {
        // Given
        val route = aValidRoute(originHubId = "   ")

        // When
        val result = validator(route)

        // Then
        val error = errorsOf(result).single()
        assertIs<ValidationField.Blank>(error)
        assertEquals(EntityField.ORIGIN_HUB_ID, error.field)
    }

    @Test
    fun `returns Blank error on DESTINATION_HUB_ID when destination hub id is empty`() {
        // Given
        val route = aValidRoute(destinationHubId = "")

        // When
        val result = validator(route)

        // Then
        val error = errorsOf(result).single()
        assertIs<ValidationField.Blank>(error)
        assertEquals(EntityField.DESTINATION_HUB_ID, error.field)
    }

    @Test
    fun `returns NotPositive error on DISTANCE_KM when distance is zero`() {
        // Given
        val route = aValidRoute(distanceKm = 0.0)

        // When
        val result = validator(route)

        // Then
        val error = errorsOf(result).single()
        assertIs<ValidationField.NotPositive>(error)
        assertEquals(EntityField.DISTANCE_KM, error.field)
        assertEquals(0.0, error.actualValue)
    }

    @Test
    fun `returns NotPositive error on DISTANCE_KM when distance is negative`() {
        // Given
        val route = aValidRoute(distanceKm = -50.0)

        // When
        val result = validator(route)

        // Then
        val error = errorsOf(result).single()
        assertIs<ValidationField.NotPositive>(error)
        assertEquals(EntityField.DISTANCE_KM, error.field)
        assertEquals(-50.0, error.actualValue)
    }

    @Test
    fun `returns Negative error on TYPICAL_DELAY_MIN when delay is negative`() {
        // Given
        val route = aValidRoute(typicalDelayMin = -5.0)

        // When
        val result = validator(route)

        // Then
        val error = errorsOf(result).single()
        assertIs<ValidationField.Negative>(error)
        assertEquals(EntityField.TYPICAL_DELAY_MIN, error.field)
        assertEquals(-5.0, error.actualValue)
    }

    @Test
    fun `returns all errors when every field is invalid`() {
        // Given
        val route = aValidRoute(
            originHubId = "",
            destinationHubId = "",
            distanceKm = 0.0,
            typicalDelayMin = -1.0
        )

        // When
        val result = validator(route)

        // Then
        assertEquals(
            listOf(
                EntityField.ORIGIN_HUB_ID,
                EntityField.DESTINATION_HUB_ID,
                EntityField.DISTANCE_KM,
                EntityField.TYPICAL_DELAY_MIN
            ),
            errorsOf(result).map { it.field }
        )
    }

    private fun aValidRoute(
        originHubId: String = "WH-001",
        destinationHubId: String = "WH-002",
        distanceKm: Double = 120.0,
        typicalDelayMin: Double = 15.0
    ) = Route(
        id = "RT-001",
        originHub = aWarehouse(originHubId),
        destinationHub = aWarehouse(destinationHubId),
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin
    )

    private fun aWarehouse(id: String) = Warehouse(
        id = id,
        name = "Hub $id",
        regionalZone = RegionalZone.NORTH,
        latitude = 32.22,
        longitude = 35.26
    )

    private fun errorsOf(result: ValidationResult): List<ValidationField> =
        assertIs<ValidationResult.Invalid>(result).errors
}
