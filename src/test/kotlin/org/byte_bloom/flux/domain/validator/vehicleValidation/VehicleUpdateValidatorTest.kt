package org.byte_bloom.flux.domain.validator.vehicleValidation

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.request.VehicleUpdateRequest
import org.byte_bloom.flux.domain.validation.EntityField
import org.byte_bloom.flux.domain.validation.ValidationField
import org.byte_bloom.flux.domain.validation.ValidationResult


class VehicleUpdateValidatorTest {

    private val validator = VehicleUpdateValidator()


    // ---------- Happy paths: any single valid field is enough ----------

    @Test
    fun `returns Valid when only hub id is provided`() {
        // Given
        val request = aRequest(currentHubId = "WH-002")

        // When
        val result = validator(request)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `returns Valid when only capacity is provided`() {
        // Given
        val request = aRequest(maxCapacityKg = 800.0)

        // When
        val result = validator(request)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `returns Valid when only cost is provided`() {
        // Given
        val request = aRequest(costPerKm = 3.5)

        // When
        val result = validator(request)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `returns Valid when all fields are provided and valid`() {
        // Given
        val request = aRequest(currentHubId = "WH-002", maxCapacityKg = 800.0, costPerKm = 3.5)

        // When
        val result = validator(request)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    // ---------- No field updated ----------

    @Test
    fun `returns only NoFieldUpdated when every field is null`() {
        // Given
        val request = aRequest()

        // When
        val result = validator(request)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.NoFieldUpdated::class.java)
        assertThat(error.field).isEqualTo(EntityField.NONE)
    }

    // ---------- Hub id ----------

    @Test
    fun `returns only Blank error when hub id is empty and nothing else is provided`() {
        // Given
        val request = aRequest(currentHubId = "")

        // When
        val result = validator(request)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.Blank::class.java)
        assertThat(error.field).isEqualTo(EntityField.CURRENT_HUB_ID)
    }

    @Test
    fun `returns only Blank error when hub id is whitespace and nothing else is provided`() {
        // Given
        val request = aRequest(currentHubId = "   ")

        // When
        val result = validator(request)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.Blank::class.java)
        assertThat(error.field).isEqualTo(EntityField.CURRENT_HUB_ID)
    }

    @Test
    fun `returns only Blank error when hub id is empty but there is a valid field provided`() {
        // Given
        val request = aRequest(currentHubId = "", maxCapacityKg = 800.0)

        // When
        val result = validator(request)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.Blank::class.java)
        assertThat(error.field).isEqualTo(EntityField.CURRENT_HUB_ID)
    }

    // ---------- Capacity ----------

    @Test
    fun `returns NotPositive error on MAX_CAPACITY_KG when capacity is zero`() {
        // Given
        val request = aRequest(maxCapacityKg = 0.0)

        // When
        val result = validator(request)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.NotPositive::class.java)
        assertThat(error.field).isEqualTo(EntityField.MAX_CAPACITY_KG)
    }


    @Test
    fun `returns NotPositive error on MAX_CAPACITY_KG when capacity is negative`() {
        // Given
        val request = aRequest(maxCapacityKg = -10.0)

        // When
        val result = validator(request)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.NotPositive::class.java)
        assertThat(error.field).isEqualTo(EntityField.MAX_CAPACITY_KG)
        //assertThat(error.actualValue).isEqualTo(-10.0) TODO
    }

    @Test
    fun `returns Valid when capacity is just above zero`() {
        // Given
        val request = aRequest(maxCapacityKg = 0.1)

        // When
        val result = validator(request)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `returns NotPositive error on MAX_CAPACITY_KG when capacity is NaN`() { //todo
        // Given
        val request = aRequest(maxCapacityKg = Double.NaN)

        // When
        val result = validator(request)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.NotPositive::class.java)
        assertThat(error.field).isEqualTo(EntityField.MAX_CAPACITY_KG)
    }

    // ---------- Cost per km ----------

    @Test
    fun `returns NotPositive error on COST_PER_KM when cost is zero`() {
        // Given
        val request = aRequest(costPerKm = 0.0)

        // When
        val result = validator(request)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.NotPositive::class.java)
        assertThat(error.field).isEqualTo(EntityField.COST_PER_KM)
    }

    @Test
    fun `returns NotPositive error on COST_PER_KM when cost is negative`() {
        // Given
        val request = aRequest(costPerKm = -2.0)

        // When
        val result = validator(request)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.NotPositive::class.java)
        assertThat(error.field).isEqualTo(EntityField.COST_PER_KM)
        //assertThat(error.actualValue).isEqualTo(-2.0) TODO
    }

    @Test
    fun `returns Valid when cost is just above zero`() {
        // Given
        val request = aRequest(costPerKm = 0.1)

        // When
        val result = validator(request)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    // ---------- Multiple errors ----------

    @Test
    fun `reports both errors and no NoFieldUpdated when capacity and cost are invalid`() {
        // Given
        val request = aRequest(maxCapacityKg = 0.0, costPerKm = -1.0)

        // When
        val result = validator(request)

        // Then
        val errors = errorsOf(result)
        assertThat(errors).hasSize(2)
        assertThat(errors.map { it.field }).containsExactly(
            EntityField.MAX_CAPACITY_KG,
            EntityField.COST_PER_KM
        )
        errors.forEach {
            assertThat(it).isNotInstanceOf(ValidationField.NoFieldUpdated::class.java)
        }


    }

    // ---------- Helpers ----------

    private fun aRequest(
        currentHubId: String? = null,
        maxCapacityKg: Double? = null,
        costPerKm: Double? = null
    ) = VehicleUpdateRequest(currentHubId, maxCapacityKg, costPerKm)

    private fun errorsOf(result: ValidationResult): List<ValidationField> {
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        return (result as ValidationResult.Invalid).errors
    }


}



