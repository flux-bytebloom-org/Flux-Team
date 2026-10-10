package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority

private const val URGENT_PRIORITY_RANK = 3
private const val STANDARD_PRIORITY_RANK = 2
private const val LOW_PRIORITY_RANK = 1
private const val DEFAULT_WEIGHT_FOR_MISSING_VALUE = -1.0
private const val FIRST_PACKAGE_INDEX = 0
private const val NEXT_INDEX_OFFSET = 1
private const val PREVIOUS_INDEX_OFFSET = -1
private const val LAST_INDEX_OFFSET = -1

class SortPackagesByPriorityAndWeightUseCase {

    operator fun invoke(packages: List<Package>): List<Package> {
        val mutablePackages = packages.toMutableList()
        performSelectionSort(mutablePackages)
        return mutablePackages
    }

    private fun performSelectionSort(packages: MutableList<Package>) {
        val lastPackageIndex = packages.size + LAST_INDEX_OFFSET

        for (currentIndex in FIRST_PACKAGE_INDEX until lastPackageIndex) {
            val highestIndex = findHighestPackageIndex(packages, currentIndex)

            if (highestIndex != currentIndex) {
                movePackageToPosition(packages, highestIndex, currentIndex)
            }
        }
    }

    private fun findHighestPackageIndex(packages: List<Package>, startIndex: Int): Int {
        var highestIndex = startIndex

        val firstComparisonIndex = startIndex + NEXT_INDEX_OFFSET
        val lastPackageIndex = packages.size + LAST_INDEX_OFFSET

        for (index in firstComparisonIndex..lastPackageIndex) {
            if (hasHigherPriorityThenWeight(packages[index], packages[highestIndex])) {
                highestIndex = index
            }
        }

        return highestIndex
    }

    private fun movePackageToPosition(packages: MutableList<Package>, fromIndex: Int, toIndex: Int) {
        val packageToMove = packages[fromIndex]

        for (index in fromIndex downTo toIndex + NEXT_INDEX_OFFSET) {
            packages[index] = packages[index + PREVIOUS_INDEX_OFFSET]
        }

        packages[toIndex] = packageToMove
    }

    private fun hasHigherPriorityThenWeight(first: Package, second: Package): Boolean {
        val firstRank = getPriorityRank(first.priority)
        val secondRank = getPriorityRank(second.priority)

        return when {
            firstRank > secondRank -> true
            firstRank == secondRank ->
                (first.weight ?: DEFAULT_WEIGHT_FOR_MISSING_VALUE) >
                        (second.weight ?: DEFAULT_WEIGHT_FOR_MISSING_VALUE)

            else -> false
        }
    }

    private fun getPriorityRank(priority: Priority): Int = when (priority) {
        Priority.URGENT -> URGENT_PRIORITY_RANK
        Priority.STANDARD -> STANDARD_PRIORITY_RANK
        Priority.LOW -> LOW_PRIORITY_RANK
    }
}