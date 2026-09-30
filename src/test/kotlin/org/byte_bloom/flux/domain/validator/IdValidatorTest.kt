package org.byte_bloom.flux.domain.validator

import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.validation.EntityPrefixes
import org.byte_bloom.flux.domain.validation.ValidationField
import org.byte_bloom.flux.domain.validation.ValidationResult
import org.junit.jupiter.api.Test

class IdValidatorTest {

    private val validator = IdValidator(EntityPrefixes.PACKAGE)

    @Test
    fun `given id with correct prefix when validate then returns Valid`() {
        // Given
        val id = "PKG-1"

        // When
        val result = validator(id)

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `given id with wrong prefix when validate then returns InvalidPrefix error`() {
        // Given
        val id = "V-1"

        // When
        val result = validator(id)

        // Then
        val errors = (result as ValidationResult.Invalid).errors
        assertThat(errors.map { it::class })
            .containsExactly(ValidationField.InvalidPrefix::class)
    }

    @Test
    fun `given blank id when validate then returns Blank and InvalidPrefix errors`() {
        // Given
        val id = "   "

        // When
        val result = validator(id)

        // Then
        val errors = (result as ValidationResult.Invalid).errors
        assertThat(errors.map { it::class })
            .containsExactly(ValidationField.Blank::class, ValidationField.InvalidPrefix::class)
    }
}