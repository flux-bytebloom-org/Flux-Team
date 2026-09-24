package org.byte_bloom.flux.ui.scenarios

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.PackageRepository
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.usecase.crud.DeletePackageUseCase
import org.byte_bloom.flux.domain.usecase.crud.pakage.CreatePackageUseCase
import org.byte_bloom.flux.domain.usecase.crud.pakage.GetPackageByIdUseCase
import org.byte_bloom.flux.domain.usecase.crud.pakage.UpdatePackageUseCase
import org.byte_bloom.flux.domain.validator.packagevalidations.PackageCreateValidator
import org.byte_bloom.flux.domain.validator.packagevalidations.PackageUpdateValidator

suspend fun testPackageCrudFlow(
    packageRepository: PackageRepository,
    warehouseRepository: WarehouseRepository
) {
    val createUC = CreatePackageUseCase(packageRepository, PackageCreateValidator())
    val getByIdUC = GetPackageByIdUseCase(packageRepository)
    val updateUC =
        UpdatePackageUseCase(packageRepository, warehouseRepository, updateValidator = PackageUpdateValidator())
    val deleteUC = DeletePackageUseCase(packageRepository)

    println("--- Sub-Task 4: Package Result-based error handling ---")

    val warehouses = warehouseRepository.getAll()
    val origin = warehouses.getOrNull(0)
    val destination = warehouses.getOrNull(1)

    if (origin == null || destination == null) {
        println("Skipping package scenario: need at least two warehouses to test with")
        return
    }

    // Scenario 1: successful create
    val createResult = createUC(
        Package(id = "", weight = 4.5, originHub = origin, destinationHub = destination, priority = Priority.STANDARD)
    )
    createResult
        .onSuccess { created -> println("Created: $created") }
        .onFailure { error -> printLogisticsError(error) }

    // Scenario 2: deliberate validation failure (blank hub ids, negative weight)
    val invalidWarehouse = Warehouse(id = "", name = "", regionalZone = RegionalZone.UnKNOWN, latitude = 0.0, longitude = 0.0)
    createUC(
        Package(id = "", weight = -5.0, originHub = invalidWarehouse, destinationHub = invalidWarehouse, priority = Priority.STANDARD)
    ).onSuccess { println("Unexpected success on invalid input") }
        .onFailure { error -> printLogisticsError(error) }

    // Scenario 3: id not found
    getByIdUC("PKG-does-not-exist")
        .onSuccess { println("Unexpected success finding unknown id") }
        .onFailure { error -> printLogisticsError(error) }
}

private fun printLogisticsError(error: Throwable) {
    val message = when (error) {
        is LogisticsException.ValidationException.EntityValidationException ->
            "Validation error: ${error.errors.joinToString(", ")}"

        is LogisticsException.EntityNotFoundException.PackageNotFoundException ->
            "Not found: package id = ${error.id}"

        is LogisticsException.EntityNotFoundException.WarehouseNotFoundException ->
            "Referenced warehouse not found: id = ${error.id}"

        is LogisticsException.DataAccessException ->
            "Data access problem: ${error.message}"

        else ->
            "Unexpected error: ${error.message}"
    }
    println(message)
}
