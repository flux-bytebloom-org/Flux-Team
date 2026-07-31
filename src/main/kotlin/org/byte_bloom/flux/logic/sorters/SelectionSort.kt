package org.byte_bloom.flux.logic.sorters

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority

private const val URGENT_PRIORITY_RANK = 3
private const val STANDARD_PRIORITY_RANK = 2
private const val LOW_PRIORITY_RANK = 1

fun sortByPriorityAndWeightDescending(packages: List<Package>): List<Package> {

    val sortedPackages = packages.toMutableList()

    performSelectionSort(sortedPackages)

    return sortedPackages
}


private fun performSelectionSort(packages: MutableList<Package>) {

    val firstPackageIndex = 0
    val lastPackageIndex = packages.size - 1

    for (currentIndex in firstPackageIndex until lastPackageIndex) {

        val highestIndex = findHighestPackageIndex(
            packages,
            currentIndex
        )

        if (highestIndex != currentIndex) {
            movePackageToPosition(
                packages,
                highestIndex,
                currentIndex
            )
        }
    }
}


private fun findHighestPackageIndex(
    packages: List<Package>,
    startIndex: Int
): Int {

    var highestIndex = startIndex

    val firstComparisonIndex = startIndex + 1
    val lastPackageIndex = packages.size - 1

    for (index in firstComparisonIndex..lastPackageIndex) {

        if (hasHigherPriorityThenWeight(
                packages[index],
                packages[highestIndex]
            )
        ) {
            highestIndex = index
        }
    }

    return highestIndex
}


private fun movePackageToPosition(
    packages: MutableList<Package>,
    fromIndex: Int,
    toIndex: Int
) {

    val packageToMove = packages[fromIndex]

    for (index in fromIndex downTo toIndex + 1) {
        packages[index] = packages[index - 1]
    }

    packages[toIndex] = packageToMove
}


private fun hasHigherPriorityThenWeight(
    first: Package,
    second: Package
): Boolean {

    val firstRank = getPriorityRank(first.priority)
    val secondRank = getPriorityRank(second.priority)
    /*
    return when {
        firstRank > secondRank -> true
        firstRank == secondRank -> first.weight > second.weight
        else -> false
    }*/
    return when {
        firstRank > secondRank -> true
        firstRank == secondRank ->
            (first.weight ?: -1.0) > (second.weight ?: -1.0)
        else -> false
    }
}


private fun getPriorityRank(priority: Priority): Int {

    return when (priority) {
        Priority.URGENT -> URGENT_PRIORITY_RANK
        Priority.STANDARD -> STANDARD_PRIORITY_RANK
        Priority.LOW -> LOW_PRIORITY_RANK
    }
}
