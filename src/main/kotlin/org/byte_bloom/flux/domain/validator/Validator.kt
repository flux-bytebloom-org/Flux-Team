package org.byte_bloom.flux.domain.validator

import org.byte_bloom.flux.domain.validation.ValidationResult

fun interface Validator<T> {
    operator fun invoke(value: T): ValidationResult
}
