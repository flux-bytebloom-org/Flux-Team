package org.byte_bloom.flux.data.parsers

import org.byte_bloom.flux.data.dataholders.VehicleRaw

private const val VEHICLE_COLUMN_COUNT = 4
private const val VEHICLE_ID_INDEX = 0
private const val HUB_ID_INDEX = 1
private const val CAPACITY_INDEX = 2
private const val COST_INDEX = 3


fun parseFleet(lines: List<String>): List<VehicleRaw> {

    val fleet = mutableListOf<VehicleRaw>()

    for (line in lines) {
        val vehicle = parseVehicleLine(line)

        if (vehicle != null) {
            fleet.add(vehicle)
        }
    }
    return fleet
}


private fun parseVehicleLine(rawLine: String): VehicleRaw? {

    val columns = splitColumns(rawLine)

    if (!hasValidColumnCount(
            columns,
            expectedColumnCount = VEHICLE_COLUMN_COUNT,
            rawLine,
            rowType = "vehicle"
        ) ||
        !hasRequiredVehicleData(columns, rawLine)
    ) {
        return null
    }

    return createVehicle(columns, rawLine)
}


private fun hasRequiredVehicleData(
    columns: List<String>,
    rawLine: String
): Boolean {

    val vehicleId = columns[VEHICLE_ID_INDEX]
    val hubId = columns[HUB_ID_INDEX]

    if (vehicleId.isEmpty() || hubId.isEmpty()) {

        logWarning(
            "Missing fleet data: $rawLine"
        )

        return false
    }

    return true
}


private fun createVehicle(
    columns: List<String>,
    rawLine: String
): VehicleRaw {

    val vehicleId = columns[VEHICLE_ID_INDEX]
    val hubId = columns[HUB_ID_INDEX]

    val capacity = parseDoubleOrDefault(
        columns[CAPACITY_INDEX],
        "capacity",
        rawLine
    )

    val cost = parseDoubleOrDefault(
        columns[COST_INDEX],
        "cost",
        rawLine
    )

    return VehicleRaw(
        vehicleId,
        hubId,
        capacity,
        cost
    )
}

