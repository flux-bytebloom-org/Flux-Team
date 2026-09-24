package org.byte_bloom.flux.domain.validator.warehouseValidations

import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.validator.EntityField
import org.byte_bloom.flux.domain.validator.FieldChecks
import org.byte_bloom.flux.domain.validator.ValidationResult
import org.byte_bloom.flux.domain.validator.Validator

class WarehouseCreateValidator : Validator<Warehouse> {

    override operator fun invoke(value: Warehouse): ValidationResult {
        val errors = listOfNotNull(FieldChecks.notBlank(EntityField.WAREHOUSE_NAME, value.name),
            FieldChecks.inRange(EntityField.LATITUDE, value.latitude, -90.0, 90.0),
            FieldChecks.inRange(EntityField.LONGITUDE, value.longitude, -180.0, 180.0)
        )

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}
