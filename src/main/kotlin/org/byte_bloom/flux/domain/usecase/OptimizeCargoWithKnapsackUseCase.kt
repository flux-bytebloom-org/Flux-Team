package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority

class OptimizeCargoWithKnapsackUseCase {

    operator fun invoke(packages: List<Package>, capacity: Double): List<Package> {
        val capacityUnits = capacity.toInt()
        val grid = Array(packages.size + 1) { IntArray(capacityUnits + 1) }

        for (row in 1..packages.size) {
            val pkg = packages[row - 1]
            val weight = (pkg.weight ?: 0.0).toInt()
            val value = when (pkg.priority) {
                Priority.URGENT -> 3
                Priority.STANDARD -> 2
                Priority.LOW -> 1
            }
            for (limit in 0..capacityUnits) {
                grid[row][limit] = grid[row - 1][limit]
                if (weight <= limit) {
                    grid[row][limit] = maxOf(grid[row][limit], value + grid[row - 1][limit - weight])
                }
            }
        }

        val selected = mutableListOf<Package>()
        var remaining = capacityUnits
        for (row in packages.size downTo 1) {
            if (grid[row][remaining] != grid[row - 1][remaining]) {
                val pkg = packages[row - 1]
                selected.add(0, pkg)
                remaining -= (pkg.weight ?: 0.0).toInt()
            }
        }
        return selected
    }
}