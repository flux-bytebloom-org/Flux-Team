package org.byte_bloom.flux.domain.validator.routevalidation

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.testdata.createRouteTestWarehouse
import org.byte_bloom.flux.domain.testdata.createTestRoute
import org.byte_bloom.flux.domain.validation.EntityField
import org.byte_bloom.flux.domain.validation.ValidationField
import org.byte_bloom.flux.domain.validation.ValidationResult
import org.junit.jupiter.api.Test

class RouteCreateValidatorTest {

    private val validator = RouteCreateValidator()

    private fun errorsOf(result: ValidationResult) = (result as ValidationResult.Invalid).errors

    @Test
    fun `given valid route when validate then returns Valid`() {
        // Given
        val route = createTestRoute()

        // When
        val result = validator(route)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `given zero typical delay when validate then returns Valid`() {
        // Given
        val route = createTestRoute(typicalDelayMin = 0.0)

        // When
        val result = validator(route)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `given blank origin hub id when validate then returns Blank error`() {
        // Given
        val route = createTestRoute(origin = createRouteTestWarehouse(" "))

        // When
        val result = validator(route)

        // Then
        val errors = errorsOf(result)
        assertThat(errors.map { it::class }).containsExactly(ValidationField.Blank::class)
        assertThat(errors.first().field).isEqualTo(EntityField.ORIGIN_HUB_ID)
    }

    @Test
    fun `given blank destination hub id when validate then returns Blank error`() {
        // Given
        val route = createTestRoute(destination = createRouteTestWarehouse(""))

        // When
        val result = validator(route)

        // Then
        val errors = errorsOf(result)
        assertThat(errors.map { it::class }).containsExactly(ValidationField.Blank::class)
        assertThat(errors.first().field).isEqualTo(EntityField.DESTINATION_HUB_ID)
    }

    @Test
    fun `given zero distance when validate then returns NotPositive error`() {
        // Given
        val route = createTestRoute(distanceKm = 0.0)

        // When
        val result = validator(route)

        // Then
        val errors = errorsOf(result)
        assertThat(errors.map { it::class }).containsExactly(ValidationField.NotPositive::class)
        assertThat(errors.first().field).isEqualTo(EntityField.DISTANCE_KM)
    }

    @Test
    fun `given negative distance when validate then returns NotPositive error`() {
        // Given
        val route = createTestRoute(distanceKm = -50.0)

        // When
        val result = validator(route)

        // Then
        val error = errorsOf(result).single() as ValidationField.NotPositive
        assertThat(error.field).isEqualTo(EntityField.DISTANCE_KM)
        assertThat(error.actualValue).isEqualTo(-50.0)
    }

    @Test
    fun `given negative typical delay when validate then returns Negative error`() {
        // Given
        val route = createTestRoute(typicalDelayMin = -5.0)

        // When
        val result = validator(route)

        // Then
        val error = errorsOf(result).single() as ValidationField.Negative
        assertThat(error.field).isEqualTo(EntityField.TYPICAL_DELAY_MIN)
        assertThat(error.actualValue).isEqualTo(-5.0)
    }

    @Test
    fun `given multiple invalid fields when validate then returns all errors`() {
        // Given
        val route = createTestRoute(
            origin = createRouteTestWarehouse(""),
            destination = createRouteTestWarehouse(""),
            distanceKm = 0.0,
            typicalDelayMin = -1.0
        )

        // When
        val result = validator(route)

        // Then
        assertThat(errorsOf(result).map { it.field }).containsExactly(
            EntityField.ORIGIN_HUB_ID,
            EntityField.DESTINATION_HUB_ID,
            EntityField.DISTANCE_KM,
            EntityField.TYPICAL_DELAY_MIN
        ).inOrder()
    }
}
