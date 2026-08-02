package org.byte_bloom.flux.data.loaders

import org.byte_bloom.flux.data.dataholders.PackageRaw
import org.byte_bloom.flux.data.dataholders.RouteRaw
import org.byte_bloom.flux.data.dataholders.VehicleRaw
import org.byte_bloom.flux.data.dataholders.WarehouseRaw
import org.byte_bloom.flux.data.parsers.cleanLines
import org.byte_bloom.flux.data.parsers.parseFleet
import org.byte_bloom.flux.data.parsers.parsePackages
import org.byte_bloom.flux.data.parsers.parseRoutes
import org.byte_bloom.flux.data.parsers.parseWarehouses
import org.byte_bloom.flux.data.readers.readCsv

fun loadPackages(): List<PackageRaw> {
    val lines = readCsv("src/main/resources/packages.csv")
    val cleanLines = cleanLines(lines)
    return parsePackages(cleanLines)
}

fun loadWarehouses(): List<WarehouseRaw> {
    val lines = readCsv("src/main/resources/warehouses.csv")
    val cleanLines = cleanLines(lines)
    return parseWarehouses(cleanLines)
}

fun loadRoutes(): List<RouteRaw> {
    val routeLines = readCsv("src/main/resources/routes.csv")
    val cleanRouteLines = cleanLines(routeLines)
    return parseRoutes(cleanRouteLines)
}

fun loadFleet(): List<VehicleRaw> {
    val fleetLines = readCsv("src/main/resources/fleet.csv")
    val cleanFleetLines = cleanLines(fleetLines)
    return parseFleet(cleanFleetLines)
}
