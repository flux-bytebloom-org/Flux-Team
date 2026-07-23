package org.byte_bloom.flux.data.parsers


import org.byte_bloom.flux.data.dataholders.Warehouse
import org.byte_bloom.flux.utils.printWarningLogger

private const val WAREHOUSE_COLUMN_COUNT = 3

private const val WAREHOUSE_ID_INDEX = 0
private const val WAREHOUSE_NAME_INDEX = 1
private const val WAREHOUSE_REGIONAL_ZONE_INDEX = 2

fun parseWarehouses(rawWarehouseLines: List<String>): List<Warehouse> {

    val warehouses = mutableListOf<Warehouse>()

    for (rawLine in rawWarehouseLines) {

        val warehouse = parseWarehouseLine(rawLine)

        if (warehouse != null) {
            warehouses.add(warehouse)
        }
    }

    return warehouses
}

private fun parseWarehouseLine(rawLine: String): Warehouse? {

    val columns = splitColumns(rawLine)

    if (!hasValidColumnCount(columns, WAREHOUSE_COLUMN_COUNT, rawLine, "warehouse")) {
        return null
    }

    if (!hasRequiredWarehouseData(columns, rawLine)) {
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

        printWarningLogger(
            "Missing warehouse id: $line"
        )

        return false
    }

    return true
}

private fun createWarehouse(
    columns: List<String>
): Warehouse {

    val warehouseId = columns[WAREHOUSE_ID_INDEX]
    val warehouseName = columns[WAREHOUSE_NAME_INDEX]
    val warehouseRegionalZone = columns[WAREHOUSE_REGIONAL_ZONE_INDEX]

    return Warehouse(
        id = warehouseId,
        name = warehouseName,
        regionalZone = warehouseRegionalZone
    )
}