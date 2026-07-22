package org.byte_bloom.flux.utils.parsers

import org.byte_bloom.flux.dataholders.Vehicle
import org.byte_bloom.flux.utils.printWarningLogger


fun parseVehicles(lines: List<String>): List<Vehicle> {

    val vehicles = mutableListOf<Vehicle>()

    for (line in lines) {

        parseVehicleLine(line)?.let {
            vehicles.add(it)
        }
    }

    return vehicles
}


private fun parseVehicleLine(line: String): Vehicle? {

    val columns = splitColumns(line)

    if (!hasValidColumnCount(columns, line)) {
        return null
    }

    if (!hasRequiredVehicleData(columns, line)) {
        return null
    }

    return createVehicle(columns, line)
}


private fun hasValidColumnCount(
    columns: List<String>,
    line: String
): Boolean {

    if (columns.size != 4) {

        printWarningLogger(
            "Invalid fleet row: $line"
        )

        return false
    }

    return true
}


private fun hasRequiredVehicleData(
    columns: List<String>,
    line: String
): Boolean {

    val vehicleId = columns[0]
    val hubId = columns[1]

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

    val vehicleId = columns[0]
    val hubId = columns[1]

    val capacity = parseDoubleOrDefault(
        columns[2],
        "capacity",
        line
    )

    val cost = parseDoubleOrDefault(
        columns[3],
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