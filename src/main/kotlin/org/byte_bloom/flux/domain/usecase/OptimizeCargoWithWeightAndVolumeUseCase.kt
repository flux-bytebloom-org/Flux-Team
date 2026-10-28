package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import kotlin.math.ceil
import kotlin.math.floor

private const val URGENT_VALUE = 3
private const val STANDARD_VALUE = 2
private const val LOW_VALUE = 1

private const val MIN_VALID_WEIGHT_KG = 0.0
private const val MIN_VALID_VOLUME_M3 = 0.0
private const val MIN_CAPACITY = 0.0
private const val MISSING_WEIGHT_KG = 0.0

private const val VOLUME_UNITS_PER_M3 = 100
private const val ROUNDING_TOLERANCE = 1e-9

private const val EMPTY_SET_ROW = 1
private const val ZERO_CAPACITY_SLOT = 1

class OptimizeCargoWithWeightAndVolumeUseCase {

    operator fun invoke(
        packages: List<Package>,
        weightCapacityKg: Double,
        volumeCapacityM3: Double
    ): List<Package> {
        if (weightCapacityKg < MIN_CAPACITY || volumeCapacityM3 < MIN_CAPACITY) return emptyList()

        val usable = packages.filter { hasValidWeight(it) && hasValidVolume(it) }
        val weightCapacity = toCapacityUnits(weightCapacityKg)
        val volumeCapacity = toCapacityUnits(volumeCapacityM3 * VOLUME_UNITS_PER_M3)

        val table = buildDpTable(usable, weightCapacity, volumeCapacity)
        return backtrack(usable, table, weightCapacity, volumeCapacity)
    }

    private fun hasValidWeight(pkg: Package): Boolean {
        val weight = pkg.weight ?: return false
        return weight > MIN_VALID_WEIGHT_KG
    }

    private fun hasValidVolume(pkg: Package): Boolean =
        pkg.volumeM3 >= MIN_VALID_VOLUME_M3

    private fun buildDpTable(
        packages: List<Package>,
        weightCapacity: Int,
        volumeCapacity: Int
    ): Array<Array<IntArray>> {
        val table = Array(packages.size + EMPTY_SET_ROW) {
            Array(weightCapacity + ZERO_CAPACITY_SLOT) { IntArray(volumeCapacity + ZERO_CAPACITY_SLOT) }
        }
        packages.forEachIndexed { index, pkg ->
            fillRow(table, row = index + EMPTY_SET_ROW, pkg = pkg)
        }
        return table
    }

    private fun fillRow(table: Array<Array<IntArray>>, row: Int, pkg: Package) {
        val weight = weightUnitsOf(pkg)
        val volume = volumeUnitsOf(pkg)
        val value = valueOf(pkg.priority)
        val previous = table[row - EMPTY_SET_ROW]

        for (weightLimit in previous.indices) {
            for (volumeLimit in previous[weightLimit].indices) {
                val skip = previous[weightLimit][volumeLimit]
                val fits = weight <= weightLimit && volume <= volumeLimit
                table[row][weightLimit][volumeLimit] =
                    if (!fits) skip
                    else maxOf(skip, value + previous[weightLimit - weight][volumeLimit - volume])
            }
        }
    }

    private fun backtrack(
        packages: List<Package>,
        table: Array<Array<IntArray>>,
        weightCapacity: Int,
        volumeCapacity: Int
    ): List<Package> {
        val selected = mutableListOf<Package>()
        var remainingWeight = weightCapacity
        var remainingVolume = volumeCapacity

        for (row in packages.size downTo EMPTY_SET_ROW) {
            val isTaken = table[row][remainingWeight][remainingVolume] !=
                    table[row - EMPTY_SET_ROW][remainingWeight][remainingVolume]
            if (isTaken) {
                val pkg = packages[row - EMPTY_SET_ROW]
                selected.add(pkg)
                remainingWeight -= weightUnitsOf(pkg)
                remainingVolume -= volumeUnitsOf(pkg)
            }
        }
        return selected.reversed()
    }

    private fun weightUnitsOf(pkg: Package): Int = toItemUnits(pkg.weight ?: MISSING_WEIGHT_KG)

    private fun volumeUnitsOf(pkg: Package): Int = toItemUnits(pkg.volumeM3 * VOLUME_UNITS_PER_M3)

    private fun toItemUnits(amount: Double): Int = ceil(amount - ROUNDING_TOLERANCE).toInt()

    private fun toCapacityUnits(amount: Double): Int = floor(amount + ROUNDING_TOLERANCE).toInt()

    private fun valueOf(priority: Priority): Int = when (priority) {
        Priority.URGENT -> URGENT_VALUE
        Priority.STANDARD -> STANDARD_VALUE
        Priority.LOW -> LOW_VALUE
    }
}
