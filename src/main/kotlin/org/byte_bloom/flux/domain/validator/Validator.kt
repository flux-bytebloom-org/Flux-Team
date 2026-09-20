package org.byte_bloom.flux.domain.validator

fun interface Validator<T> {
    operator fun invoke(value: T): ValidationResult
}
