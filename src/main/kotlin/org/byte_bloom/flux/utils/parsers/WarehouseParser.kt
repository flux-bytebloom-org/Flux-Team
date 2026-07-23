import org.byte_bloom.flux.utils.parsers.splitColumns
import org.byte_bloom.flux.dataholders.Warehouse
import org.byte_bloom.flux.utils.printWarningLogger

private const val EXPECTED_WAREHOUSE_COLUMNS_COUNT = 3

fun parseWarehouses(rawWarehouseLines: List<String>): List<Warehouse> {
    val parsedWarehousesList = mutableListOf<Warehouse>()

    for (rawLine in rawWarehouseLines) {
        val parsedWarehouse = parseSingleWarehouseLine(rawLine)
        if (parsedWarehouse != null) {
            parsedWarehousesList.add(parsedWarehouse)
        }
    }
    return parsedWarehousesList
}

private fun parseSingleWarehouseLine(rawLine: String): Warehouse? {
    val warehouseColumns = splitColumns(rawLine)

    if (warehouseColumns.size != EXPECTED_WAREHOUSE_COLUMNS_COUNT) {
        printWarningLogger("Invalid warehouse row: $rawLine")
        return null
    }

    val warehouseId = warehouseColumns[0]
    val warehouseName = warehouseColumns[1]
    val warehouseZone = warehouseColumns[2]

    if (warehouseId.isEmpty()) {
        printWarningLogger("Missing warehouse id: $rawLine")
        return null
    }

    return Warehouse(
        id = warehouseId,
        name = warehouseName,
        regionalZone = warehouseZone
    )
}