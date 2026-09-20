package org.byte_bloom.flux.domain.validator

object FieldChecks {
    fun notBlank(field: EntityField, value: String): ValidationField? =
        if (value.isBlank()) ValidationField.Blank(field) else null

    fun positive(field: EntityField, value: Double?): ValidationField? =
        if (value <= 0) ValidationField.NotPositive(field, value) else null

    fun notNegative(field: EntityField, value: Double?): ValidationField? =
        if (value < 0) ValidationField.Negative(field, value) else null

    fun inRange(field: EntityField, value: Double, min: Double, max: Double): ValidationField? =
        if (value < min || value > max) ValidationField.OutOfRange(field, value, min, max) else null

    fun validPrefix(field: EntityField, value: String, prefix: String): ValidationField? =
        if (!value.startsWith(prefix)) ValidationField.InvalidPrefix(field, value, prefix) else null

    fun validFormat(field: EntityField, value: String, regex: Regex): ValidationField? =
        if (!regex.matches(value)) ValidationField.InvalidFormat(field, value) else null

    fun oneOf(field: EntityField, value: String, allowed: List<String>): ValidationField? =
        if (value !in allowed) ValidationField.InvalidEnumValue(field, value, allowed) else null
}

