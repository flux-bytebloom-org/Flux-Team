package org.byte_bloom.flux.di

import org.byte_bloom.flux.domain.validation.EntityPrefixes
import org.byte_bloom.flux.domain.validator.IdValidator
import org.byte_bloom.flux.domain.validator.packagevalidations.PackageCreateValidator
import org.byte_bloom.flux.domain.validator.packagevalidations.PackageUpdateValidator
import org.byte_bloom.flux.domain.validator.routevalidation.RouteCreateValidator
import org.byte_bloom.flux.domain.validator.routevalidation.RouteUpdateValidator
import org.byte_bloom.flux.domain.validator.vehicleValidation.VehicleCreateValidator
import org.byte_bloom.flux.domain.validator.vehicleValidation.VehicleUpdateValidator
import org.byte_bloom.flux.domain.validator.warehouseValidations.WarehouseCreateValidator
import org.byte_bloom.flux.domain.validator.warehouseValidations.WarehouseUpdateValidator
import org.koin.core.qualifier.named
import org.koin.dsl.module

val packageIdValidator = named("packageIdValidator")
val routeIdValidator = named("routeIdValidator")
val vehicleIdValidator = named("vehicleIdValidator")
val warehouseIdValidator = named("warehouseIdValidator")

val validatorModule = module {

    single { PackageCreateValidator() }
    single { RouteCreateValidator() }
    single { VehicleCreateValidator() }
    single { WarehouseCreateValidator() }

    single { PackageUpdateValidator() }
    single { RouteUpdateValidator() }
    single { VehicleUpdateValidator() }
    single { WarehouseUpdateValidator() }

    single(packageIdValidator) {
        IdValidator(EntityPrefixes.PACKAGE)
    }

    single(routeIdValidator) {
        IdValidator(EntityPrefixes.ROUTE)
    }

    single(vehicleIdValidator) {
        IdValidator(EntityPrefixes.VEHICLE)
    }

    single(warehouseIdValidator) {
        IdValidator(EntityPrefixes.WAREHOUSE)
    }
}