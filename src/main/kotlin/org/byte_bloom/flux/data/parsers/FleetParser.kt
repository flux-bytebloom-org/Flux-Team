package org.byte_bloom.flux.data.parsers

import org.byte_bloom.flux.data.dataholders.Vehicle
import org.byte_bloom.flux.utils.printWarningLogger

private const val VEHICLE_COLUMN_COUNT = 4

private const val VEHICLE_ID_INDEX = 0
private const val HUB_ID_INDEX = 1
private const val CAPACITY_INDEX = 2
private const val COST_INDEX = 3


fun parseVehicles(lines: List<String>): List<Vehicle> {

    val vehicles = mutableListOf<Vehicle>()

    for (line in lines) {
        val vehicle = parseVehicleLine(line)

        if (vehicle != null) {
            vehicles.add(vehicle)
        }
    }
    return vehicles
}


private fun parseVehicleLine(line: String): Vehicle? {

    val columns = splitColumns(line)

    if (!hasValidColumnCount(columns, VEHICLE_COLUMN_COUNT ,line , "vehicle")) {
        return null
    }

    if (!hasRequiredVehicleData(columns, line)) {
        return null
    }

    return createVehicle(columns, line)
}




private fun hasRequiredVehicleData(
    columns: List<String>,
    line: String
): Boolean {

    val vehicleId = columns[VEHICLE_ID_INDEX]
    val hubId = columns[HUB_ID_INDEX]

    if (vehicleId.isEmpty() || hubId.isEmpty()) {

        printWarningLogger(
            "Missing fleet data: $line"
        )

        return false
    }

    return true
}


private fun createVehicle(
    columns: List<String>,
    line: String
): Vehicle {

    val vehicleId = columns[VEHICLE_ID_INDEX]
    val hubId = columns[HUB_ID_INDEX]

    val capacity = parseDoubleOrDefault(
        columns[CAPACITY_INDEX],
        "capacity",
        line
    )

    val cost = parseDoubleOrDefault(
        columns[COST_INDEX],
        "cost",
        line
    )

    return Vehicle(
        vehicleId,
        hubId,
        capacity,
        cost
    )
}