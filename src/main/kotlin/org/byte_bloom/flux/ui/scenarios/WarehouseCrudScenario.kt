package org.byte_bloom.flux.ui.scenarios

import org.byte_bloom.flux.domain.exception.LogisticsException
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.repository.WarehouseRepository
import org.byte_bloom.flux.domain.usecase.crud.UpdateWarehouseUseCase
import org.byte_bloom.flux.domain.usecase.crud.warehouse.CreateWarehouseUseCase
import org.byte_bloom.flux.domain.usecase.crud.warehouse.DeleteWarehouseUseCase
import org.byte_bloom.flux.domain.usecase.crud.warehouse.GetWarehouseByIdUseCase
import org.byte_bloom.flux.domain.validator.warehouseValidations.WarehouseCreateValidator

suspend fun testWarehouseCrudFlow(repository: WarehouseRepository) {
    val createUC = CreateWarehouseUseCase(repository, WarehouseCreateValidator())
    val getByIdUC = GetWarehouseByIdUseCase(repository)
    val updateUC = UpdateWarehouseUseCase(repository)
    val deleteUC = DeleteWarehouseUseCase(repository)

    println("--- Sub-Task 4: Warehouse Result-based error handling ---")

    // Scenario 1: successful create
    val createResult = createUC(
        Warehouse(id = "", name = "Test Hub", regionalZone = RegionalZone.NORTH, latitude = 32.0, longitude = 35.0)
    )
    createResult
        .onSuccess { created -> println("Created: $created") }
        .onFailure { error -> printLogisticsError(error) }

    // Scenario 2: deliberate validation failure
    createUC(
        Warehouse(id = "", name = "", regionalZone = RegionalZone.UnKNOWN, latitude = 999.0, longitude = 999.0)
    ).onSuccess { println("Unexpected success on invalid input") }
        .onFailure { error -> printLogisticsError(error) }

    // Scenario 3: id not found
    getByIdUC("WH-does-not-exist")
        .onSuccess { println("Unexpected success finding unknown id") }
        .onFailure { error -> printLogisticsError(error) }

}

private fun printLogisticsError(error: Throwable) {
    val message = when (error) {
        is LogisticsException.ValidationException.EntityValidationException ->
            "Validation error: ${error.errors.joinToString(", ")}"

        is LogisticsException.EntityNotFoundException.WarehouseNotFoundException ->
            "Not found: warehouse id = ${error.id}"

        is LogisticsException.DataAccessException ->
            "Data access problem: ${error.message}"

        else ->
            "Unexpected error: ${error.message}"
    }
    println(message)
}
