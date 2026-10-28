package org.byte_bloom.flux.ui.scenarios

import org.byte_bloom.flux.di.useCaseModule
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.RegionalZone
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.usecase.OptimizeCargoWithKnapsackUseCase
import org.byte_bloom.flux.domain.usecase.OptimizeCargoWithWeightAndVolumeUseCase
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.java.KoinJavaComponent.getKoin

private const val DEMO_TRUCK_MAX_KG = 10.0
private const val DEMO_TRUCK_MAX_M3 = 1.0
private const val DEMO_COST_PER_KM = 1.0

private const val DEMO_HUB_LATITUDE = 0.0
private const val DEMO_HUB_LONGITUDE = 0.0

private const val BULKY_WEIGHT_KG = 1.0
private const val BULKY_VOLUME_M3 = 0.6
private const val STEEL_WEIGHT_KG = 9.0
private const val STEEL_VOLUME_M3 = 0.1
private const val ENGINE_WEIGHT_KG = 8.0
private const val ENGINE_VOLUME_M3 = 0.2

private const val MISSING_WEIGHT_KG = 0.0
private const val NUMBER_FORMAT = "%.1f"

fun runVolumeKnapsackComparisonScenario(packagePool: List<Package>, vehicle: Vehicle) {
    println("\n=== Scenario: 2D Cargo Optimizer (Weight vs Weight + Volume) ===")
    println("Vehicle ${vehicle.id}: max ${format(vehicle.maxCapacityKg)} kg, ${format(vehicle.maxVolumeM3)} m³")

    val optimizeWeightOnly: OptimizeCargoWithKnapsackUseCase = getKoin().get()
    val optimizeWithVolume: OptimizeCargoWithWeightAndVolumeUseCase = getKoin().get()

    val weightOnly = optimizeWeightOnly(packagePool, vehicle.maxCapacityKg)
    val weightAndVolume = optimizeWithVolume(packagePool, vehicle.maxCapacityKg, vehicle.maxVolumeM3)

    printPayload("Weight only", weightOnly, vehicle)
    printPayload("Weight + Volume", weightAndVolume, vehicle)
    printDifference(weightOnly, weightAndVolume)
}

private fun printPayload(title: String, payload: List<Package>, vehicle: Vehicle) {
    println("\n--- $title ---")
    payload.forEach { pkg ->
        println("  - ${pkg.id} | ${format(weightOf(pkg))} kg | ${format(pkg.volumeM3)} m³ | ${pkg.priority}")
    }

    val totalWeight = payload.sumOf { weightOf(it) }
    val totalVolume = payload.sumOf { it.volumeM3 }
    val fitsInTruck = totalVolume <= vehicle.maxVolumeM3
    println("Total: ${format(totalWeight)} kg, ${format(totalVolume)} m³ → fits in truck: ${if (fitsInTruck) "YES" else "NO"}")
}

private fun printDifference(weightOnly: List<Package>, weightAndVolume: List<Package>) {
    val removed = weightOnly.filterNot { it in weightAndVolume }.map { it.id }
    val added = weightAndVolume.filterNot { it in weightOnly }.map { it.id }

    println("\n--- What changed when volume was added ---")
    println("Removed: ${removed.ifEmpty { listOf("none") }.joinToString()}")
    println("Added:   ${added.ifEmpty { listOf("none") }.joinToString()}")
}

private fun weightOf(pkg: Package): Double = pkg.weight ?: MISSING_WEIGHT_KG

private fun format(amount: Double): String = NUMBER_FORMAT.format(amount)

private fun demoPackage(id: String, weightKg: Double, volumeM3: Double, priority: Priority, hub: Warehouse) =
    Package(
        id = id,
        weight = weightKg,
        originHub = hub,
        destinationHub = hub,
        priority = priority,
        volumeM3 = volumeM3
    )

private fun demoPackages(hub: Warehouse): List<Package> = listOf(
    demoPackage("PKG-PILLOWS", BULKY_WEIGHT_KG, BULKY_VOLUME_M3, Priority.URGENT, hub),
    demoPackage("PKG-BLANKETS", BULKY_WEIGHT_KG, BULKY_VOLUME_M3, Priority.STANDARD, hub),
    demoPackage("PKG-STEEL", STEEL_WEIGHT_KG, STEEL_VOLUME_M3, Priority.STANDARD, hub),
    demoPackage("PKG-ENGINE", ENGINE_WEIGHT_KG, ENGINE_VOLUME_M3, Priority.LOW, hub)
)

fun main() {
    startKoin { modules(useCaseModule) }

    val hub = Warehouse("WH-DEMO", "Demo Hub", RegionalZone.NORTH, DEMO_HUB_LATITUDE, DEMO_HUB_LONGITUDE)
    val truck = Vehicle(
        id = "TRK-DEMO",
        currentHub = hub,
        maxCapacityKg = DEMO_TRUCK_MAX_KG,
        costPerKm = DEMO_COST_PER_KM,
        maxVolumeM3 = DEMO_TRUCK_MAX_M3
    )
    runVolumeKnapsackComparisonScenario(demoPackages(hub), truck)

    stopKoin()
}
