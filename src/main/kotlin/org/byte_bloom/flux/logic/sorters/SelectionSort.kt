package org.byte_bloom.flux.logic.sorters

import org.byte_bloom.flux.dataholders.Package
import org.byte_bloom.flux.dataholders.Priority


fun sortByPriorityAndWeightDescending(packages: List<Package>): List<Package> {
    val sortedPackages = packages.toMutableList()

    performSelectionSort(sortedPackages)

    return sortedPackages
}


private fun performSelectionSort(packages: MutableList<Package>) {

    for (currentIndex in 0 until packages.size - 1) {

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

    for (index in startIndex + 1 until packages.size) {

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

    return when {
        firstRank > secondRank -> true
        firstRank == secondRank -> first.weight > second.weight
        else -> false
    }
}


private fun getPriorityRank(priority: Priority): Int {

    return when (priority) {
        Priority.URGENT -> 3
        Priority.STANDARD -> 2
        else -> 1
    }
}