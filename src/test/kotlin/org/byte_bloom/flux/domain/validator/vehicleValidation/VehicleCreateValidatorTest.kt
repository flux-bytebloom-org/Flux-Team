package org.byte_bloom.flux.domain.validator.vehicleValidation

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.validation.EntityField
import org.byte_bloom.flux.domain.validation.ValidationField
import org.byte_bloom.flux.domain.validation.ValidationResult
import org.junit.jupiter.api.Test

class VehicleCreateValidatorTest {

    private val validator = VehicleCreateValidator()

    // ---------- happy Case ----------
    @Test
    fun `returns Valid when all fields are valid`() {
        // Given
        val vehicle = aValidVehicle()

        // When
        val result = validator(vehicle)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }


    // ---------- Hub id ----------

    @Test
    fun `returns Blank error on CURRENT_HUB_ID when hub id is whitespace only`() {
        // Given
        val vehicle = aValidVehicle(hubId = "   ")

        // When
        val result = validator(vehicle)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.Blank::class.java)
        assertThat(error.field).isEqualTo(EntityField.CURRENT_HUB_ID)
    }


    // ---------- Capacity ----------

    @Test
    fun `returns NotPositive error on MAX_CAPACITY_KG when capacity is zero`() {
        // Given
        val vehicle = aValidVehicle(capacity = 0.0)

        // When
        val result = validator(vehicle)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.NotPositive::class.java)
        assertThat(error.field).isEqualTo(EntityField.MAX_CAPACITY_KG)
    }

    @Test
    fun `returns NotPositive error on MAX_CAPACITY_KG when capacity is negative`() {
        // Given
        val vehicle = aValidVehicle(capacity = -1.0)

        // When
        val result = validator(vehicle)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.NotPositive::class.java)
        assertThat(error.field).isEqualTo(EntityField.MAX_CAPACITY_KG)
    }

    @Test
    fun `returns NotPositive error on MAX_CAPACITY_KG when capacity is Nan`() {//todo
        // Given
        val vehicle = aValidVehicle(capacity = Double.NaN)

        // When
        val result = validator(vehicle)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.NotPositive::class.java)
        assertThat(error.field).isEqualTo(EntityField.MAX_CAPACITY_KG)
    }

    @Test
    fun `returns NotPositive error on MAX_CAPACITY_KG when capacity is Infinity`() {//todo
        // Given
        val vehicle = aValidVehicle(capacity = Double.POSITIVE_INFINITY)

        // When
        val result = validator(vehicle)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.NotPositive::class.java)
        assertThat(error.field).isEqualTo(EntityField.MAX_CAPACITY_KG)
    }

    @Test
    fun `returns Valid when capacity is just above zero`() {
        // Given
        val vehicle = aValidVehicle(capacity = 0.1)

        // When
        val result = validator(vehicle)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    // ---------- Cost per km ----------

    @Test
    fun `returns NotPositive error on COST_PER_KM when cost is zero`() {
        // Given
        val vehicle = aValidVehicle(costPerKm = 0.0)

        // When
        val result = validator(vehicle)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.NotPositive::class.java)
        assertThat(error.field).isEqualTo(EntityField.COST_PER_KM)
    }

    @Test
    fun `returns NotPositive error on COST_PER_KM when cost is negative`() {
        // Given
        val vehicle = aValidVehicle(costPerKm = -1.0)

        // When
        val result = validator(vehicle)

        // Then
        val error = errorsOf(result).single()
        assertThat(error).isInstanceOf(ValidationField.NotPositive::class.java)
        assertThat(error.field).isEqualTo(EntityField.COST_PER_KM)
        //assertThat(error.actualValue).isEqualTo(-1.0)
    }

    @Test
    fun `returns Valid when cost is just above zero`() {
        // Given
        val vehicle = aValidVehicle(costPerKm = 0.1)

        // When
        val result = validator(vehicle)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }


    // ---------- Multiple errors ----------

    @Test
    fun `reports every invalid field when many fields are invalid`() {
        // Given
        val vehicle = aValidVehicle(hubId = "", capacity = 0.0, costPerKm = -1.0)

        // When
        val result = validator(vehicle)

        // Then
        val errors = errorsOf(result)
        assertThat(errors.map { it.field }).containsExactly(
            EntityField.CURRENT_HUB_ID,
            EntityField.MAX_CAPACITY_KG,
            EntityField.COST_PER_KM
        )
    }


    // ---------- Helper methods ----------

        private fun aValidVehicle(
        hubId: String = "WH-001",capacity: Double = 500.0,costPerKm: Double = 2.0
    ) = Vehicle(
        id = "V-001",
        currentHub = Warehouse(hubId, "Test Hub", RegionalZone.NORTH, 0.0, 0.0),
        maxCapacityKg = capacity,
        costPerKm = costPerKm
    )

    private fun errorsOf(result: ValidationResult): List<ValidationField> {
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        return (result as ValidationResult.Invalid).errors
    }

}
