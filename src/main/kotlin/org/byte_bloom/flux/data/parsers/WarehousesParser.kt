package org.byte_bloom.flux.data.parsers


import org.byte_bloom.flux.data.dataholders.WarehouseRaw
import org.byte_bloom.flux.utils.logWarning

private const val WAREHOUSE_COLUMN_COUNT = 5

private const val WAREHOUSE_ID_INDEX = 0
private const val WAREHOUSE_NAME_INDEX = 1
private const val WAREHOUSE_REGIONAL_ZONE_INDEX = 2
private const val WAREHOUSE_LATITUDE_INDEX = 3
private const val WAREHOUSE_LONGITUDE_INDEX = 4

fun parseWarehouses(rawWarehouseLines: List<String>): List<WarehouseRaw> {
    val warehouses = mutableListOf<WarehouseRaw>()

    for (rawLine in rawWarehouseLines) {

        val warehouse = parseWarehouseLine(rawLine)

        if (warehouse != null) {
            warehouses.add(warehouse)
        }
    }

    return warehouses
}

private fun parseWarehouseLine(rawLine: String): WarehouseRaw? {

    val columns = splitColumns(rawLine)

    if (!hasValidColumnCount(
            columns,
            expectedColumnCount = WAREHOUSE_COLUMN_COUNT,
            rawLine,
            rowType = "warehouse"
        ) ||
        !hasRequiredWarehouseData(columns, rawLine)
    ) {
        return null
    }

    return createWarehouse(columns)
}

private fun hasRequiredWarehouseData(
    columns: List<String>,
    line: String
): Boolean {

    val warehouseId = columns[WAREHOUSE_ID_INDEX]

    if (warehouseId.isEmpty()) {

        logWarning(
            "Missing warehouse id: $line"
        )

        return false
    }

    return true
}

private fun createWarehouse(
    columns: List<String>
): WarehouseRaw {

    val warehouseId = columns[WAREHOUSE_ID_INDEX]
    val warehouseName = columns[WAREHOUSE_NAME_INDEX]
    val warehouseRegionalZone = columns[WAREHOUSE_REGIONAL_ZONE_INDEX]

    return WarehouseRaw(
        warehouseId = warehouseId,
        name = warehouseName,
        regionalZone = warehouseRegionalZone,
        latitude = columns[WAREHOUSE_LATITUDE_INDEX].toDoubleOrNull() ?: 0.0,
        longitude = columns[WAREHOUSE_LONGITUDE_INDEX].toDoubleOrNull() ?: 0.0
    )
}
