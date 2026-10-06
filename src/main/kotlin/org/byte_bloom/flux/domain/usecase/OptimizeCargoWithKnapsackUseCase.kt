package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority

private const val URGENT_VALUE = 3
private const val STANDARD_VALUE = 2
private const val LOW_VALUE = 1

class OptimizeCargoWithKnapsackUseCase {

    operator fun invoke(packages: List<Package>, capacity: Double): List<Package> {
        val capacityUnits = capacity.toInt()
        val grid = buildDpTable(packages, capacityUnits)
        return backtrack(packages, grid, capacityUnits)
    }

    private fun buildDpTable(packages: List<Package>, capacity: Int): Array<IntArray> {
        val grid = Array(packages.size + 1) { IntArray(capacity + 1) }
        packages.forEachIndexed { index, pkg ->
            val row = index + 1
            val weight = weightOf(pkg)
            val value = valueOf(pkg.priority)
            for (limit in 0..capacity) {
                val skip = grid[row - 1][limit]
                grid[row][limit] =
                    if (weight > limit) skip
                    else maxOf(skip, value + grid[row - 1][limit - weight])
            }
        }
        return grid
    }

    private fun backtrack(packages: List<Package>, grid: Array<IntArray>, capacity: Int): List<Package> {
        val selected = mutableListOf<Package>()
        var remaining = capacity
        for (row in packages.size downTo 1) {
            if (grid[row][remaining] != grid[row - 1][remaining]) {
                val pkg = packages[row - 1]
                selected.add(pkg)
                remaining -= weightOf(pkg)
            }
        }
        return selected.reversed()
    }

    private fun weightOf(pkg: Package): Int = (pkg.weight ?: 0.0).toInt()

    private fun valueOf(priority: Priority): Int = when (priority) {
        Priority.URGENT -> URGENT_VALUE
        Priority.STANDARD -> STANDARD_VALUE
        Priority.LOW -> LOW_VALUE
    }
}