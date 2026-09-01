package org.byte_bloom.flux.domain.exception

sealed class UseCaseException(message: String) : Exception(message) {
    private object Messages {
        const val WAREHOUSE_NOT_FOUND = "Warehouse not found with id:"
    }

    class WarehouseNotFound(warehouseId: String) :
        UseCaseException("${Messages.WAREHOUSE_NOT_FOUND} $warehouseId")
}
