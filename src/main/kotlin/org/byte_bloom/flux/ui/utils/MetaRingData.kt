package org.byte_bloom.flux.ui.utils

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse

object MetaRingData {

    const val FIRST_VEHICLE_INDEX = 0
    const val SECOND_VEHICLE_INDEX = 1
    const val THIRD_VEHICLE_INDEX = 2
    const val FOURTH_VEHICLE_INDEX = 3
    const val BROKEN_VEHICLE_INDEX = 1

    private const val DEFAULT_LATITUDE = 0.0
    private const val DEFAULT_LONGITUDE = 0.0

    private const val FIRST_VEHICLE_CAPACITY = 1000.0
    private const val FIRST_VEHICLE_COST_PER_KM = 5.0
    private const val SECOND_VEHICLE_CAPACITY = 1200.0
    private const val SECOND_VEHICLE_COST_PER_KM = 6.0
    private const val THIRD_VEHICLE_CAPACITY = 1500.0
    private const val THIRD_VEHICLE_COST_PER_KM = 7.0
    private const val FOURTH_VEHICLE_CAPACITY = 2000.0
    private const val FOURTH_VEHICLE_COST_PER_KM = 8.0

    private const val FIRST_PACKAGE_WEIGHT = 10.0
    private const val SECOND_PACKAGE_WEIGHT = 20.0
    private const val THIRD_PACKAGE_WEIGHT = 30.0
    private const val FOURTH_PACKAGE_WEIGHT = 15.0
    private const val FIFTH_PACKAGE_WEIGHT = 25.0

    fun createVehicles(): MutableList<Vehicle> {
        return mutableListOf(
            buildVehicle(
                "V1",
                buildWarehouse("W1", "Main", "ZoneA"),
                FIRST_VEHICLE_CAPACITY,
                FIRST_VEHICLE_COST_PER_KM
            ),
            buildVehicle(
                "V2",
                buildWarehouse("W2", "Second", "ZoneB"),
                SECOND_VEHICLE_CAPACITY,
                SECOND_VEHICLE_COST_PER_KM
            ),
            buildVehicle(
                "V3",
                buildWarehouse("W3", "Third", "ZoneC"),
                THIRD_VEHICLE_CAPACITY,
                THIRD_VEHICLE_COST_PER_KM
            ),
            buildVehicle(
                "V4",
                buildWarehouse("W4", "Fourth", "ZoneD"),
                FOURTH_VEHICLE_CAPACITY,
                FOURTH_VEHICLE_COST_PER_KM
            )
        )
    }

    fun createPackages(vehicles: List<Vehicle>): List<Package> {
        val first = vehicles[FIRST_VEHICLE_INDEX]
        val second = vehicles[SECOND_VEHICLE_INDEX]
        val third = vehicles[THIRD_VEHICLE_INDEX]
        val fourth = vehicles[FOURTH_VEHICLE_INDEX]

        return listOf(
            buildPackage("P1", FIRST_PACKAGE_WEIGHT, first, second, Priority.URGENT),
            buildPackage("P2", SECOND_PACKAGE_WEIGHT, second, third, Priority.STANDARD),
            buildPackage("P3", THIRD_PACKAGE_WEIGHT, third, fourth, Priority.LOW),
            buildPackage("P4", FOURTH_PACKAGE_WEIGHT, first, fourth, Priority.STANDARD),
            buildPackage("P5", FIFTH_PACKAGE_WEIGHT, second, first, Priority.URGENT)
        )
    }

    private fun buildWarehouse(id: String, name: String, zone: String): Warehouse {
        return Warehouse(id, name, zone, DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    }

    private fun buildVehicle(
        id: String,
        hub: Warehouse,
        capacity: Double,
        costPerKm: Double
    ): Vehicle {
        return Vehicle(id, hub, capacity, costPerKm)
    }

    private fun buildPackage(
        id: String,
        weight: Double,
        origin: Vehicle,
        destination: Vehicle,
        priority: Priority
    ): Package {
        return Package(id, weight, origin.currentHub, destination.currentHub, priority)
    }
}

