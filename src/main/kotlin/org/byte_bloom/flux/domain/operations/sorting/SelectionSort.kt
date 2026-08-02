package org.byte_bloom.flux.domain.operations.sorting

import org.byte_bloom.flux.data.dataholders.PackageRaw
import org.byte_bloom.flux.data.dataholders.Priority


fun sortByPriorityAndWeightDescending(packages: List<PackageRaw>): List<PackageRaw> {

    val sortedPackages = packages.toMutableList()

    performSelectionSort(sortedPackages)

    return sortedPackages
}


private fun performSelectionSort(packages: MutableList<PackageRaw>) {

    val firstPackageIndex = SortConstants.FIRST_PACKAGE_INDEX
    val lastPackageIndex = packages.size + SortConstants.LAST_INDEX_OFFSET

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
    packages: List<PackageRaw>,
    startIndex: Int
): Int {

    var highestIndex = startIndex

    val firstComparisonIndex = startIndex + SortConstants.NEXT_INDEX_OFFSET
    val lastPackageIndex = packages.size + SortConstants.LAST_INDEX_OFFSET

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
    packages: MutableList<PackageRaw>,
    fromIndex: Int,
    toIndex: Int
) {

    val packageToMove = packages[fromIndex]

    for (index in fromIndex downTo toIndex + SortConstants.NEXT_INDEX_OFFSET) {
        packages[index] = packages[index + SortConstants.PREVIOUS_INDEX_OFFSET]
    }

    packages[toIndex] = packageToMove
}


private fun hasHigherPriorityThenWeight(
    first: PackageRaw,
    second: PackageRaw
): Boolean {

    val firstRank = getPriorityRank(first.priority)
    val secondRank = getPriorityRank(second.priority)

    return when {
        firstRank > secondRank -> true
        firstRank == secondRank ->
            (first.weight ?: SortConstants.DEFAULT_WEIGHT_FOR_MISSING_VALUE) > (second.weight
                ?: SortConstants.DEFAULT_WEIGHT_FOR_MISSING_VALUE)

        else -> false
    }
}


private fun getPriorityRank(priority: Priority): Int {

    return when (priority) {
        Priority.URGENT -> SortConstants.URGENT_PRIORITY_RANK
        Priority.STANDARD -> SortConstants.STANDARD_PRIORITY_RANK
        Priority.LOW -> SortConstants.LOW_PRIORITY_RANK
    }
}

private object SortConstants {
    const val URGENT_PRIORITY_RANK = 3
    const val STANDARD_PRIORITY_RANK = 2
    const val LOW_PRIORITY_RANK = 1
    const val DEFAULT_WEIGHT_FOR_MISSING_VALUE = -1.0
    const val FIRST_PACKAGE_INDEX = 0
    const val NEXT_INDEX_OFFSET = 1
    const val PREVIOUS_INDEX_OFFSET = -1
    const val LAST_INDEX_OFFSET = -1
}
