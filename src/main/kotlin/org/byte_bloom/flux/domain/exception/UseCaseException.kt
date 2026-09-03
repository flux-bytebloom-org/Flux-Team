package org.byte_bloom.flux.domain.exception

sealed class UseCaseException(message: String) : Exception(message) {

    class WarehouseNotFound(warehouseId: String) :
        UseCaseException("${Messages.WAREHOUSE_NOT_FOUND} $warehouseId")

    class InvalidPackageWeight(reason: String) :
        UseCaseException("${Messages.INVALID_PACKAGE_WEIGHT} $reason")

    class InvalidRequiredWeight(reason: String) :
        UseCaseException("${Messages.INVALID_REQUIRED_WEIGHT} $reason")

    class NoStationedVehicles(warehouseId: String) :
        UseCaseException("${Messages.NO_STATIONED_VEHICLES} $warehouseId")

    class PackageNotInQueue(packageId: String, warehouseId: String) :
        UseCaseException("${Messages.PACKAGE_NOT_IN_QUEUE} package=$packageId warehouse=$warehouseId")

    private object Messages {
        const val WAREHOUSE_NOT_FOUND = "Warehouse not found with id:"
        const val INVALID_PACKAGE_WEIGHT = "Invalid package weight:"
        const val INVALID_REQUIRED_WEIGHT = "Invalid required weight:"
        const val NO_STATIONED_VEHICLES = "No stationed vehicles at warehouse:"
        const val PACKAGE_NOT_IN_QUEUE = "Package not found in warehouse queue:"
    }
}
