package org.byte_bloom.flux.domain.validator

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val errors: List<ValidationField>) : ValidationResult()
}
enum class EntityField {
    WAREHOUSE_NAME, LATITUDE, LONGITUDE, REGIONAL_ZONE,
    CURRENT_HUB_ID, MAX_CAPACITY_KG, COST_PER_KM,
    ORIGIN_HUB_ID, DESTINATION_HUB_ID, DISTANCE_KM, TYPICAL_DELAY_MIN,
    WEIGHT, PRIORITY, ID , NONE
}

sealed class ValidationField(val field: EntityField) {
    class Blank(field: EntityField) : ValidationField(field)

    class NotPositive(field: EntityField, val actualValue: Double) : ValidationField(field)

    class Negative(field: EntityField, val actualValue: Double) : ValidationField(field)

    class OutOfRange(field: EntityField, val actualValue: Double, val min: Double, val max: Double) : ValidationField(field)

    class InvalidPrefix(field: EntityField, val actualValue: String, val expectedPrefix: String) : ValidationField(field)

    class InvalidFormat(field: EntityField, val actualValue: String) : ValidationField(field)

    class MissingReference(field: EntityField, val referencedId: String) : ValidationField(field)

    class NoFieldUpdated (field : EntityField = EntityField.NONE):ValidationField(field= EntityField.NONE)

    class InvalidEnumValue(field: EntityField, val actualValue: String, val allowedValues: List<String>) : ValidationField(field)
}

