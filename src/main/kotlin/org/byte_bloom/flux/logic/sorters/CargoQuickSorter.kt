/**
 * WHY THIS IMPLEMENTATION:
 * 1. In-Place Sorting: Modifies list directly to
 *    minimize memory allocations.
 * 2. Tailrec Partitioning: Eliminates stack overflow
 *    risks during deep recursive sorting.
 * 3. Safe Null Handling: Defaults missing weights to 0.0
 *    to push invalid entries to the end safely.
 */
package org.byte_bloom.flux.logic.sorters

import org.byte_bloom.flux.domain.model.Package

private const val DEFAULT_WEIGHT_FOR_MISSING_VALUE = 0.0
private const val MIN_PACKAGES_TO_SORT = 1
private const val FIRST_INDEX = 0
private const val INDEX_OFFSET = 1

fun sortCargoByWeightDescending(cargoQueue: MutableList<Package>) {
    val totalPackages = cargoQueue.size

    if (totalPackages <= MIN_PACKAGES_TO_SORT) return

    quickSortPackages(cargoQueue, startIndex = FIRST_INDEX, endIndex = totalPackages - INDEX_OFFSET)
}

private fun quickSortPackages(cargoQueue: MutableList<Package>, startIndex: Int, endIndex: Int) {
    if (startIndex < endIndex) {
        val pivotIndex = partitionCargoByWeightDescending(
            cargoQueue = cargoQueue,
            startIndex = startIndex,
            endIndex = endIndex,
            currentIndex = startIndex,
            boundaryIndex = startIndex - INDEX_OFFSET
        )

        quickSortPackages(cargoQueue, startIndex, pivotIndex - INDEX_OFFSET)
        quickSortPackages(cargoQueue, pivotIndex + INDEX_OFFSET, endIndex)
    }
}

private tailrec fun partitionCargoByWeightDescending(
    cargoQueue: MutableList<Package>,
    startIndex: Int,
    endIndex: Int,
    currentIndex: Int,
    boundaryIndex: Int
): Int {
    if (currentIndex >= endIndex) {
        swapPackages(cargoQueue, boundaryIndex + INDEX_OFFSET, endIndex)
        return boundaryIndex + INDEX_OFFSET
    }

    val currentWeight = cargoQueue[currentIndex].weight ?: DEFAULT_WEIGHT_FOR_MISSING_VALUE
    val pivotWeight = cargoQueue[endIndex].weight ?: DEFAULT_WEIGHT_FOR_MISSING_VALUE

    val nextBoundary = if (currentWeight >= pivotWeight) {
        swapPackages(cargoQueue, boundaryIndex + INDEX_OFFSET, currentIndex)
        boundaryIndex + INDEX_OFFSET
    } else {
        boundaryIndex
    }

    return partitionCargoByWeightDescending(
        cargoQueue = cargoQueue,
        startIndex = startIndex,
        endIndex = endIndex,
        currentIndex = currentIndex + INDEX_OFFSET,
        boundaryIndex = nextBoundary
    )
}

private fun swapPackages(cargoQueue: MutableList<Package>, firstIndex: Int, secondIndex: Int) {
    if (firstIndex != secondIndex) {
        val temporaryPackageHolder = cargoQueue[firstIndex]
        cargoQueue[firstIndex] = cargoQueue[secondIndex]
        cargoQueue[secondIndex] = temporaryPackageHolder
    }
}
