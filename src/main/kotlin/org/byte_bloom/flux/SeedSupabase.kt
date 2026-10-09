/**
 * SeedSupabase.kt
 *
 * Reads the four Flux CSV exports (warehouses, fleet, routes, packages), cleans and
 * validates every row, and generates two files:
 *   scripts/supabase_seed.sql   -> ready to paste into the Supabase SQL Editor
 *   scripts/seed_report.txt     -> summary + row-by-row reasons for every excluded row
 *
 * This script never talks to Supabase directly and never modifies the source CSVs.
 *
 * Usage:
 *   kotlinc SeedSupabase.kt -include-runtime -d SeedSupabase.jar
 *   java -jar SeedSupabase.jar [csvDir] [outputDir]
 *
 *   csvDir    default: "csv"      must contain warehouses.csv, fleet.csv, routes.csv, packages.csv
 *   outputDir default: "scripts"  supabase_seed.sql and seed_report.txt are written here
 */

import java.io.File
import java.time.LocalDateTime

// ============================================================================
// Generic CSV reading (hand-rolled: no external dependency, handles quoted
// fields, embedded commas, CRLF line endings, and blank lines).
// ============================================================================

/** One data row from a CSV file, keeping its original 1-based line number for reporting. */
data class CsvRow(val lineNumber: Int, val fields: List<String>)

data class CsvTable(val header: List<String>, val rows: List<CsvRow>, val blankLinesSkipped: Int)

private fun parseCsvLine(line: String): List<String> {
    val fields = mutableListOf<String>()
    val sb = StringBuilder()
    var inQuotes = false
    var i = 0
    while (i < line.length) {
        val c = line[i]
        if (inQuotes) {
            if (c == '"') {
                if (i + 1 < line.length && line[i + 1] == '"') {
                    sb.append('"')
                    i++
                } else {
                    inQuotes = false
                }
            } else {
                sb.append(c)
            }
        } else {
            when (c) {
                '"' -> inQuotes = true
                ',' -> {
                    fields.add(sb.toString())
                    sb.clear()
                }
                else -> sb.append(c)
            }
        }
        i++
    }
    fields.add(sb.toString())
    return fields
}

private fun readCsvTable(path: String): CsvTable {
    val file = File(path)
    require(file.exists()) { "CSV file not found: $path" }

    val rawLines = file.readLines(Charsets.UTF_8).map { it.removeSuffix("\r") }
    require(rawLines.isNotEmpty()) { "CSV file is empty: $path" }

    val header = parseCsvLine(rawLines.first()).map { it.trim() }
    val rows = mutableListOf<CsvRow>()
    var blankSkipped = 0

    for (i in 1 until rawLines.size) {
        val raw = rawLines[i]
        val lineNumber = i + 1 // 1-based, matches what a text editor shows
        if (raw.isBlank()) {
            blankSkipped++
            continue
        }
        rows.add(CsvRow(lineNumber, parseCsvLine(raw)))
    }
    return CsvTable(header, rows, blankSkipped)
}

// ============================================================================
// Normalization helpers
// ============================================================================

private fun norm(value: String?): String = (value ?: "").trim()

/** Treats blank, "N/A", "NA" and "null" (any case) as an absent value. */
private fun isMissing(value: String): Boolean {
    val v = value.trim()
    return v.isEmpty() || v.equals("N/A", ignoreCase = true) ||
            v.equals("NA", ignoreCase = true) || v.equals("null", ignoreCase = true)
}

private fun parseDoubleOrNull(value: String): Double? =
    if (isMissing(value)) null else value.trim().toDoubleOrNull()

private fun field(row: CsvRow, index: Int): String = norm(row.fields.getOrElse(index) { "" })

// ============================================================================
// Validation result type
// ============================================================================

/** A single validation failure: a short machine-readable code plus a human-readable detail. */
data class Reason(val code: String, val detail: String)

sealed class RowOutcome<out T> {
    data class Valid<T>(val value: T) : RowOutcome<T>()
    data class Invalid(val reasons: List<Reason>) : RowOutcome<Nothing>()
}

// ============================================================================
// Domain seed models (already clean — exactly what gets written to SQL)
// ============================================================================

data class WarehouseSeed(
    val id: String,
    val name: String,
    val regionalZone: String,
    val latitude: Double,
    val longitude: Double
)

data class VehicleSeed(
    val id: String,
    val currentHubId: String,
    val maxCapacityKg: Double,
    val costPerKm: Double
)

data class RouteSeed(
    val id: String,
    val originHubId: String,
    val destinationHubId: String,
    val distanceKm: Double,
    val typicalDelayMin: Double
)

data class PackageSeed(
    val id: String,
    val weight: Double,
    val originHubId: String,
    val destinationHubId: String,
    val priority: String
)

// ============================================================================
// Row validators
// ============================================================================

private val VALID_ZONES = setOf("NORTH", "SOUTH", "EAST", "WEST", "CENTRAL")

// Only the priorities that actually exist in the app's domain model (Priority enum:
// LOW, STANDARD, URGENT). Any other value found in the CSV (EXPRESS, CRITICAL, VIP,
// NONE, ...) is reported and excluded — we do not invent a mapping for them.
private val VALID_PRIORITIES = setOf("LOW", "STANDARD", "URGENT")

private fun validateWarehouseRow(row: CsvRow, seenIds: MutableSet<String>): RowOutcome<WarehouseSeed> {
    val id = field(row, 0).uppercase()
    val name = field(row, 1)
    val zoneRaw = field(row, 2)
    val latRaw = field(row, 3)
    val lonRaw = field(row, 4)

    val reasons = mutableListOf<Reason>()

    if (id.isEmpty()) reasons += Reason("MISSING_ID", "missing id")
    if (name.isEmpty()) reasons += Reason("MISSING_NAME", "missing name")

    val zone = zoneRaw.uppercase()
    if (isMissing(zoneRaw)) {
        reasons += Reason("MISSING_ZONE", "missing regionalZone")
    } else if (zone !in VALID_ZONES) {
        reasons += Reason("UNKNOWN_ZONE", "unrecognized regionalZone '$zoneRaw' (expected one of $VALID_ZONES)")
    }

    val lat = parseDoubleOrNull(latRaw)
    if (isMissing(latRaw)) {
        reasons += Reason("MISSING_LAT", "missing latitude")
    } else if (lat == null) {
        reasons += Reason("INVALID_LAT", "latitude '$latRaw' is not numeric")
    } else if (lat < -90.0 || lat > 90.0) {
        reasons += Reason("LAT_OUT_OF_RANGE", "latitude $lat is out of range [-90, 90]")
    }

    val lon = parseDoubleOrNull(lonRaw)
    if (isMissing(lonRaw)) {
        reasons += Reason("MISSING_LON", "missing longitude")
    } else if (lon == null) {
        reasons += Reason("INVALID_LON", "longitude '$lonRaw' is not numeric")
    } else if (lon < -180.0 || lon > 180.0) {
        reasons += Reason("LON_OUT_OF_RANGE", "longitude $lon is out of range [-180, 180]")
    }

    if (id.isNotEmpty() && !seenIds.add(id)) {
        reasons += Reason("DUPLICATE_ID", "duplicate id '$id' (a warehouse with this id was already seeded from an earlier row)")
    }

    return if (reasons.isEmpty())
        RowOutcome.Valid(WarehouseSeed(id, name, zone, lat!!, lon!!))
    else
        RowOutcome.Invalid(reasons)
}

private fun validateVehicleRow(
    row: CsvRow,
    validWarehouseIds: Set<String>,
    seenIds: MutableSet<String>
): RowOutcome<VehicleSeed> {
    val id = field(row, 0).uppercase()
    val hub = field(row, 1).uppercase()
    val capacityRaw = field(row, 2)
    val costRaw = field(row, 3)

    val reasons = mutableListOf<Reason>()

    if (id.isEmpty()) reasons += Reason("MISSING_ID", "missing id")

    if (isMissing(hub)) {
        reasons += Reason("MISSING_HUB", "missing currentHubId")
    } else if (hub !in validWarehouseIds) {
        reasons += Reason(
            "INVALID_HUB_REF",
            "currentHubId '$hub' does not reference a warehouse that was successfully seeded"
        )
    }

    val capacity = parseDoubleOrNull(capacityRaw)
    if (isMissing(capacityRaw)) {
        reasons += Reason("MISSING_CAPACITY", "missing maxCapacityKg")
    } else if (capacity == null) {
        reasons += Reason("INVALID_CAPACITY", "maxCapacityKg '$capacityRaw' is not numeric")
    } else if (capacity <= 0.0) {
        reasons += Reason("NON_POSITIVE_CAPACITY", "maxCapacityKg $capacity must be greater than 0")
    }

    val cost = parseDoubleOrNull(costRaw)
    if (isMissing(costRaw)) {
        reasons += Reason("MISSING_COST", "missing costPerKm")
    } else if (cost == null) {
        reasons += Reason("INVALID_COST", "costPerKm '$costRaw' is not numeric")
    } else if (cost <= 0.0) {
        reasons += Reason("NON_POSITIVE_COST", "costPerKm $cost must be greater than 0")
    }

    if (id.isNotEmpty() && !seenIds.add(id)) {
        reasons += Reason("DUPLICATE_ID", "duplicate id '$id' (a vehicle with this id was already seeded from an earlier row)")
    }

    return if (reasons.isEmpty())
        RowOutcome.Valid(VehicleSeed(id, hub, capacity!!, cost!!))
    else
        RowOutcome.Invalid(reasons)
}

private fun validateRouteRow(
    row: CsvRow,
    validWarehouseIds: Set<String>,
    seenIds: MutableSet<String>
): RowOutcome<RouteSeed> {
    val id = field(row, 0).uppercase()
    val origin = field(row, 1).uppercase()
    val destination = field(row, 2).uppercase()
    val distanceRaw = field(row, 3)
    val delayRaw = field(row, 4)

    val reasons = mutableListOf<Reason>()

    if (id.isEmpty()) reasons += Reason("MISSING_ID", "missing id")

    if (isMissing(origin)) {
        reasons += Reason("MISSING_ORIGIN", "missing originHubId")
    } else if (origin !in validWarehouseIds) {
        reasons += Reason("INVALID_ORIGIN_REF", "originHubId '$origin' does not reference a warehouse that was successfully seeded")
    }

    if (isMissing(destination)) {
        reasons += Reason("MISSING_DESTINATION", "missing destinationHubId")
    } else if (destination !in validWarehouseIds) {
        reasons += Reason(
            "INVALID_DESTINATION_REF",
            "destinationHubId '$destination' does not reference a warehouse that was successfully seeded"
        )
    }

    if (!isMissing(origin) && !isMissing(destination) && origin == destination) {
        reasons += Reason("SAME_ORIGIN_DESTINATION", "originHubId and destinationHubId are the same ('$origin')")
    }

    val distance = parseDoubleOrNull(distanceRaw)
    if (isMissing(distanceRaw)) {
        reasons += Reason("MISSING_DISTANCE", "missing distanceKm")
    } else if (distance == null) {
        reasons += Reason("INVALID_DISTANCE", "distanceKm '$distanceRaw' is not numeric")
    } else if (distance <= 0.0) {
        reasons += Reason("NON_POSITIVE_DISTANCE", "distanceKm $distance must be greater than 0")
    }

    val delay = parseDoubleOrNull(delayRaw)
    if (isMissing(delayRaw)) {
        reasons += Reason("MISSING_DELAY", "missing typicalDelayMin")
    } else if (delay == null) {
        reasons += Reason("INVALID_DELAY", "typicalDelayMin '$delayRaw' is not numeric")
    } else if (delay < 0.0) {
        reasons += Reason("NEGATIVE_DELAY", "typicalDelayMin $delay must not be negative")
    }

    if (id.isNotEmpty() && !seenIds.add(id)) {
        reasons += Reason("DUPLICATE_ID", "duplicate id '$id' (a route with this id was already seeded from an earlier row)")
    }

    return if (reasons.isEmpty())
        RowOutcome.Valid(RouteSeed(id, origin, destination, distance!!, delay!!))
    else
        RowOutcome.Invalid(reasons)
}

private fun validatePackageRow(
    row: CsvRow,
    validWarehouseIds: Set<String>,
    seenIds: MutableSet<String>
): RowOutcome<PackageSeed> {
    val id = field(row, 0).uppercase()
    val weightRaw = field(row, 1)
    val origin = field(row, 2).uppercase()
    val destination = field(row, 3).uppercase()
    val priorityRaw = field(row, 4)

    val reasons = mutableListOf<Reason>()

    if (id.isEmpty()) reasons += Reason("MISSING_ID", "missing id")

    val weight = parseDoubleOrNull(weightRaw)
    if (isMissing(weightRaw)) {
        reasons += Reason("MISSING_WEIGHT", "missing weight")
    } else if (weight == null) {
        reasons += Reason("INVALID_WEIGHT", "weight '$weightRaw' is not numeric")
    } else if (weight <= 0.0) {
        reasons += Reason("NON_POSITIVE_WEIGHT", "weight $weight must be greater than 0")
    }

    if (isMissing(origin)) {
        reasons += Reason("MISSING_ORIGIN", "missing originHubId")
    } else if (origin !in validWarehouseIds) {
        reasons += Reason("INVALID_ORIGIN_REF", "originHubId '$origin' does not reference a warehouse that was successfully seeded")
    }

    if (isMissing(destination)) {
        reasons += Reason("MISSING_DESTINATION", "missing destinationHubId")
    } else if (destination !in validWarehouseIds) {
        reasons += Reason(
            "INVALID_DESTINATION_REF",
            "destinationHubId '$destination' does not reference a warehouse that was successfully seeded"
        )
    }

    if (!isMissing(origin) && !isMissing(destination) && origin == destination) {
        reasons += Reason("SAME_ORIGIN_DESTINATION", "originHubId and destinationHubId are the same ('$origin')")
    }

    val priority = priorityRaw.trim().uppercase()
    if (isMissing(priorityRaw)) {
        reasons += Reason("MISSING_PRIORITY", "missing priority")
    } else if (priority !in VALID_PRIORITIES) {
        reasons += Reason(
            "UNKNOWN_PRIORITY",
            "priority '$priorityRaw' is not one of the app's supported values $VALID_PRIORITIES " +
                    "(found in CSV but not mapped, per instructions no value was invented for it)"
        )
    }

    if (id.isNotEmpty() && !seenIds.add(id)) {
        reasons += Reason("DUPLICATE_ID", "duplicate id '$id' (a package with this id was already seeded from an earlier row)")
    }

    return if (reasons.isEmpty())
        RowOutcome.Valid(PackageSeed(id, weight!!, origin, destination, priority))
    else
        RowOutcome.Invalid(reasons)
}

// ============================================================================
// Generic processing: run every row of a table through its validator and
// split the results into (valid values, invalid rows-with-reasons).
// ============================================================================

private fun <T> process(rows: List<CsvRow>, validate: (CsvRow) -> RowOutcome<T>): Pair<List<T>, List<Pair<CsvRow, List<Reason>>>> {
    val valid = mutableListOf<T>()
    val invalid = mutableListOf<Pair<CsvRow, List<Reason>>>()
    for (row in rows) {
        when (val outcome = validate(row)) {
            is RowOutcome.Valid -> valid += outcome.value
            is RowOutcome.Invalid -> invalid += row to outcome.reasons
        }
    }
    return valid to invalid
}

// ============================================================================
// SQL generation
// ============================================================================

private fun sqlStr(value: String): String = "'" + value.replace("'", "''") + "'"

private fun sqlNum(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()

private fun <T> buildInsertBlock(
    table: String,
    columns: List<String>,
    rows: List<T>,
    chunkSize: Int = 500,
    toValues: (T) -> String
): String = buildString {
    if (rows.isEmpty()) {
        appendLine("-- no valid rows for $table, nothing to insert")
        appendLine()
        return@buildString
    }
    rows.chunked(chunkSize).forEach { chunk ->
        appendLine("INSERT INTO $table (${columns.joinToString(", ")})")
        appendLine("VALUES")
        appendLine(chunk.joinToString(",\n") { "  (${toValues(it)})" })
        appendLine("ON CONFLICT (id) DO NOTHING;")
        appendLine()
    }
}

private fun buildSql(
    warehouses: List<WarehouseSeed>,
    vehicles: List<VehicleSeed>,
    routes: List<RouteSeed>,
    packages: List<PackageSeed>
): String = buildString {
    appendLine("-- ============================================================")
    appendLine("-- Generated by SeedSupabase.kt — do not edit by hand.")
    appendLine("-- Run manually in the Supabase SQL Editor.")
    appendLine("-- ON CONFLICT (id) DO NOTHING makes this script safe to re-run.")
    appendLine("-- Insert order respects foreign keys: warehouses -> vehicles/routes -> packages.")
    appendLine("-- ============================================================")
    appendLine()
    appendLine("BEGIN;")
    appendLine()

    appendLine("-- ===================== warehouses (${warehouses.size} valid rows) =====================")
    append(
        buildInsertBlock("warehouses", listOf("id", "name", "regional_zone", "latitude", "longitude"), warehouses) {
            "${sqlStr(it.id)}, ${sqlStr(it.name)}, ${sqlStr(it.regionalZone)}, ${sqlNum(it.latitude)}, ${sqlNum(it.longitude)}"
        }
    )

    appendLine("-- ===================== vehicles (${vehicles.size} valid rows) =====================")
    append(
        buildInsertBlock("vehicles", listOf("id", "current_hub_id", "max_capacity_kg", "cost_per_km"), vehicles) {
            "${sqlStr(it.id)}, ${sqlStr(it.currentHubId)}, ${sqlNum(it.maxCapacityKg)}, ${sqlNum(it.costPerKm)}"
        }
    )

    appendLine("-- ===================== routes (${routes.size} valid rows) =====================")
    append(
        buildInsertBlock(
            "routes",
            listOf("id", "origin_hub_id", "destination_hub_id", "distance_km", "typical_delay_min"),
            routes
        ) {
            "${sqlStr(it.id)}, ${sqlStr(it.originHubId)}, ${sqlStr(it.destinationHubId)}, ${sqlNum(it.distanceKm)}, ${sqlNum(it.typicalDelayMin)}"
        }
    )

    appendLine("-- ===================== packages (${packages.size} valid rows) =====================")
    append(
        buildInsertBlock(
            "packages",
            listOf("id", "weight", "origin_hub_id", "destination_hub_id", "priority"),
            packages
        ) {
            "${sqlStr(it.id)}, ${sqlNum(it.weight)}, ${sqlStr(it.originHubId)}, ${sqlStr(it.destinationHubId)}, ${sqlStr(it.priority)}"
        }
    )

    appendLine("COMMIT;")
}

// ============================================================================
// Report generation
// ============================================================================

private fun StringBuilder.appendFileSection(
    title: String,
    totalDataRows: Int,
    blankLinesSkipped: Int,
    validCount: Int,
    issues: List<Pair<CsvRow, List<Reason>>>
) {
    appendLine(title)
    appendLine("-".repeat(72))
    appendLine("total data rows read     : $totalDataRows")
    appendLine("blank lines skipped       : $blankLinesSkipped (formatting only, not counted as data problems)")
    appendLine("valid rows (seeded)       : $validCount")
    appendLine("excluded rows             : ${issues.size}")
    appendLine()

    if (issues.isEmpty()) {
        appendLine("No excluded rows.")
        appendLine()
        return
    }

    val byReason = mutableMapOf<String, Int>()
    for ((_, reasons) in issues) {
        for (reason in reasons) {
            byReason[reason.code] = (byReason[reason.code] ?: 0) + 1
        }
    }

    appendLine("Excluded rows by reason (a row can have more than one reason):")
    byReason.entries.sortedByDescending { it.value }.forEach { (code, count) ->
        appendLine("  %-28s %d".format(code, count))
    }
    appendLine()

    appendLine("Row-by-row detail:")
    issues.sortedBy { it.first.lineNumber }.forEach { (row, reasons) ->
        val idShown = row.fields.getOrElse(0) { "" }.trim().ifEmpty { "(none)" }
        appendLine("  line ${row.lineNumber} | id=$idShown")
        reasons.forEach { reason -> appendLine("      - ${reason.detail}") }
    }
    appendLine()
}

private fun buildReport(
    warehouseTable: CsvTable, warehouseValid: List<WarehouseSeed>, warehouseIssues: List<Pair<CsvRow, List<Reason>>>,
    vehicleTable: CsvTable, vehicleValid: List<VehicleSeed>, vehicleIssues: List<Pair<CsvRow, List<Reason>>>,
    routeTable: CsvTable, routeValid: List<RouteSeed>, routeIssues: List<Pair<CsvRow, List<Reason>>>,
    packageTable: CsvTable, packageValid: List<PackageSeed>, packageIssues: List<Pair<CsvRow, List<Reason>>>
): String = buildString {
    appendLine("FLUX — Supabase Seed Report")
    appendLine("Generated: ${LocalDateTime.now()}")
    appendLine("=".repeat(72))
    appendLine()

    appendLine("SUMMARY")
    appendLine("-".repeat(72))
    appendLine("%-16s %10s %10s %10s".format("file", "total", "valid", "excluded"))
    appendLine("%-16s %10d %10d %10d".format("warehouses.csv", warehouseTable.rows.size, warehouseValid.size, warehouseIssues.size))
    appendLine("%-16s %10d %10d %10d".format("fleet.csv", vehicleTable.rows.size, vehicleValid.size, vehicleIssues.size))
    appendLine("%-16s %10d %10d %10d".format("routes.csv", routeTable.rows.size, routeValid.size, routeIssues.size))
    appendLine("%-16s %10d %10d %10d".format("packages.csv", packageTable.rows.size, packageValid.size, packageIssues.size))
    appendLine()

    appendFileSection("WAREHOUSES.CSV", warehouseTable.rows.size, warehouseTable.blankLinesSkipped, warehouseValid.size, warehouseIssues)
    appendFileSection("FLEET.CSV (vehicles)", vehicleTable.rows.size, vehicleTable.blankLinesSkipped, vehicleValid.size, vehicleIssues)
    appendFileSection("ROUTES.CSV", routeTable.rows.size, routeTable.blankLinesSkipped, routeValid.size, routeIssues)
    appendFileSection("PACKAGES.CSV", packageTable.rows.size, packageTable.blankLinesSkipped, packageValid.size, packageIssues)
}

// ============================================================================
// main
// ============================================================================

fun main(args: Array<String>) {
    val csvDir = args.getOrNull(0) ?: "src/main/resources"
    val outputDir = args.getOrNull(1) ?: "scripts"
    File(outputDir).mkdirs()

    // ---- 1. Warehouses (no dependency on anything else) ----
    val warehouseTable = readCsvTable("$csvDir/warehouses.csv")
    val seenWarehouseIds = mutableSetOf<String>()
    val (validWarehousesFinal, warehouseIssuesFinal) =
        process(warehouseTable.rows) { validateWarehouseRow(it, seenWarehouseIds) }

    val validWarehouseIds = validWarehousesFinal.map { it.id }.toSet()

    // ---- 2. Vehicles (depends on validWarehouseIds) ----
    val vehicleTable = readCsvTable("$csvDir/fleet.csv")
    val seenVehicleIds = mutableSetOf<String>()
    val (validVehicles, vehicleIssues) =
        process(vehicleTable.rows) { validateVehicleRow(it, validWarehouseIds, seenVehicleIds) }

    // ---- 3. Routes (depends on validWarehouseIds) ----
    val routeTable = readCsvTable("$csvDir/routes.csv")
    val seenRouteIds = mutableSetOf<String>()
    val (validRoutes, routeIssues) =
        process(routeTable.rows) { validateRouteRow(it, validWarehouseIds, seenRouteIds) }

    // ---- 4. Packages (depends on validWarehouseIds) ----
    val packageTable = readCsvTable("$csvDir/packages.csv")
    val seenPackageIds = mutableSetOf<String>()
    val (validPackages, packageIssues) =
        process(packageTable.rows) { validatePackageRow(it, validWarehouseIds, seenPackageIds) }

    // ---- Write outputs ----
    val sql = buildSql(validWarehousesFinal, validVehicles, validRoutes, validPackages)
    File(outputDir, "supabase_seed.sql").writeText(sql)

    val report = buildReport(
        warehouseTable, validWarehousesFinal, warehouseIssuesFinal,
        vehicleTable, validVehicles, vehicleIssues,
        routeTable, validRoutes, routeIssues,
        packageTable, validPackages, packageIssues
    )
    File(outputDir, "seed_report.txt").writeText(report)

    // ---- Console summary ----
    println("Done.")
    println("warehouses : ${validWarehousesFinal.size} valid / ${warehouseTable.rows.size} total (${warehouseIssuesFinal.size} excluded)")
    println("vehicles   : ${validVehicles.size} valid / ${vehicleTable.rows.size} total (${vehicleIssues.size} excluded)")
    println("routes     : ${validRoutes.size} valid / ${routeTable.rows.size} total (${routeIssues.size} excluded)")
    println("packages   : ${validPackages.size} valid / ${packageTable.rows.size} total (${packageIssues.size} excluded)")
    println("Wrote $outputDir/supabase_seed.sql and $outputDir/seed_report.txt")
}
