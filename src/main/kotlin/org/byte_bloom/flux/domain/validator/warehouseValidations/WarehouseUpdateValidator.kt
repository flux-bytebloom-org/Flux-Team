package org.byte_bloom.flux.domain.validator.warehouseValidations

import org.byte_bloom.flux.domain.validator.EntityField
import org.byte_bloom.flux.domain.validator.FieldChecks
import org.byte_bloom.flux.domain.validator.ValidationField
import org.byte_bloom.flux.domain.validator.ValidationResult
import org.byte_bloom.flux.domain.validator.Validator

class WarehouseUpdateValidator : Validator<WarehouseUpdateRequest> {

    override operator fun invoke(
        warehouseUpdateReq: WarehouseUpdateRequest
    ): ValidationResult {
        val errors = listOfNotNull(
            warehouseUpdateReq.name?.let {
                FieldChecks.notBlank(
                    EntityField.WAREHOUSE_NAME,
                    it
                )
            },

            warehouseUpdateReq.regionalZone?.let {
                FieldChecks.notBlank(
                    EntityField.REGIONAL_ZONE,
                    it
                )
            },

            warehouseUpdateReq.latitude?.let {
                FieldChecks.inRange(
                    EntityField.LATITUDE,
                    it,
                    -90.0,
                    90.0
                )
            },

            warehouseUpdateReq.longitude?.let {
                FieldChecks.inRange(
                    EntityField.LONGITUDE,
                    it,
                    -180.0,
                    180.0
                )
            },

            validateAtLeastOneField(warehouseUpdateReq)
        )

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }

    private fun validateAtLeastOneField(
        request: WarehouseUpdateRequest
    ): ValidationField? = with(request) {
        val allBlank =
            name.isNullOrBlank() &&
                    regionalZone.isNullOrBlank() &&
                    latitude == null &&
                    longitude == null

        return if (allBlank) {
            ValidationField.NoFieldUpdated()
        } else {
            null
        }
    }
}