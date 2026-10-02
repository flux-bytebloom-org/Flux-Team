package org.byte_bloom.flux.domain.validator.packagevalidations

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.validation.EntityField
import org.byte_bloom.flux.domain.validation.ValidationField
import org.byte_bloom.flux.domain.validation.ValidationResult
import org.junit.jupiter.api.Test

class PackageCreateValidatorTest {

    private val validator = PackageCreateValidator()

    private fun warehouse(id: String) =
        Warehouse(id, "Test Warehouse", RegionalZone.CENTRAL, 0.0, 0.0)

    private fun pkg(
        weight: Double? = 10.0,
        originId: String = "WH-1",
        destinationId: String = "WH-2"
    ) = Package("PKG-1", weight, warehouse(originId), warehouse(destinationId), Priority.STANDARD)

    @Test
    fun `given valid package when validate then returns Valid`() {
        // Given
        val pkg = pkg()

        // When
        val result = validator(pkg)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `given zero weight when validate then returns NotPositive error on WEIGHT`() {
        // Given
        val pkg = pkg(weight = 0.0)

        // When
        val result = validator(pkg)

        // Then
        val errors = (result as ValidationResult.Invalid).errors
        assertThat(errors.map { it::class }).containsExactly(ValidationField.NotPositive::class)
        assertThat(errors.single().field).isEqualTo(EntityField.WEIGHT)
    }

    @Test
    fun `given negative weight when validate then returns NotPositive error`() {
        // Given
        val pkg = pkg(weight = -5.0)

        // When
        val result = validator(pkg)

        // Then
        val errors = (result as ValidationResult.Invalid).errors
        assertThat(errors.map { it::class }).containsExactly(ValidationField.NotPositive::class)
    }

    @Test
    fun `given blank origin hub id when validate then returns Blank error on ORIGIN_HUB_ID`() {
        // Given
        val pkg = pkg(originId = "")

        // When
        val result = validator(pkg)

        // Then
        val errors = (result as ValidationResult.Invalid).errors
        assertThat(errors.map { it::class }).containsExactly(ValidationField.Blank::class)
        assertThat(errors.single().field).isEqualTo(EntityField.ORIGIN_HUB_ID)
    }

    @Test
    fun `given blank destination hub id when validate then returns Blank error on DESTINATION_HUB_ID`() {
        // Given
        val pkg = pkg(destinationId = " ")

        // When
        val result = validator(pkg)

        // Then
        val errors = (result as ValidationResult.Invalid).errors
        assertThat(errors.single().field).isEqualTo(EntityField.DESTINATION_HUB_ID)
    }

    @Test
    fun `given multiple invalid fields when validate then returns all errors`() {
        // Given
        val pkg = pkg(weight = -1.0, originId = "", destinationId = "")

        // When
        val result = validator(pkg)

        // Then
        val errors = (result as ValidationResult.Invalid).errors
        assertThat(errors.map { it.field })
            .containsExactly(EntityField.ORIGIN_HUB_ID, EntityField.DESTINATION_HUB_ID, EntityField.WEIGHT)
    }

    @Test
    fun `given null weight when validate then returns Valid (current behavior)`() {
        // Given
        val pkg = pkg(weight = null)

        // When
        val result = validator(pkg)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }
}