package org.byte_bloom.flux.domain.exception


sealed class LogisticsException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    sealed class EntityNotFoundException(message: String) : LogisticsException(message) {
        class WarehouseNotFoundException(val id: String) :
            EntityNotFoundException("Warehouse not found with id: $id")

        class VehicleNotFoundException(val id: String) :
            EntityNotFoundException("Vehicle not found with id: $id")

        class RouteNotFoundException(val id: String) :
            EntityNotFoundException("Route not found with id: $id")

        class PackageNotFoundException(val id: String) :
            EntityNotFoundException("Package not found with id: $id")
    }


    sealed class ValidationException(message: String) : LogisticsException(message) {
        class EntityValidationException(val errors: List<String>) :
            ValidationException("Validation failed: ${errors.joinToString(", ")}")

        class InvalidCapacityException(val capacity: Double) :
            ValidationException("Invalid capacity: $capacity")

        class InvalidPackageWeightException(val reason: String) :
            ValidationException("Invalid package weight: $reason")

        class InvalidRequiredWeightException(val reason: String) :
            ValidationException("Invalid required weight: $reason")

        class InvalidTransitLoadException(val reason: String) :
            ValidationException("Invalid transit load: $reason")
    }


    sealed class DataAccessException(message: String, cause: Throwable? = null) : LogisticsException(message, cause) {
        class NetworkUnavailableException(cause: Throwable? = null) :
            DataAccessException("Network unavailable. Check your internet connection.", cause)

        class DatabaseConflictException(val reason: String, cause: Throwable? = null) :
            DataAccessException("Database conflict: $reason", cause)
    }


    sealed class BusinessLogicException(message: String) : LogisticsException(message) {
        class NoStationedVehiclesException(val warehouseId: String) :
            BusinessLogicException("No stationed vehicles at warehouse: $warehouseId")

        class ZeroFleetCapacityException(val warehouseId: String) :
            BusinessLogicException("Zero fleet capacity at warehouse: $warehouseId")

        class NoSuitableVehicleException(val warehouseId: String) :
            BusinessLogicException("No suitable vehicle found at warehouse: $warehouseId")

        class PackageNotInQueueException(val packageId: String, val warehouseId: String) :
            BusinessLogicException("Package not found in queue: package=$packageId warehouse=$warehouseId")

        class RootHubNotFoundException :
            BusinessLogicException("Root hub not found")

        class CommandExecutionException(val packageId: String) :
            BusinessLogicException("Command execution failed for package: $packageId")
    }
}