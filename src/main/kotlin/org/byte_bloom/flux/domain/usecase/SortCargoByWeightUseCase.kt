package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.model.Package

private const val DEFAULT_WEIGHT_FOR_MISSING_VALUE = -1.0
private const val MIN_PACKAGES_TO_SORT = 1
private const val FIRST_INDEX = 0
private const val INDEX_OFFSET = 1

class SortCargoByWeightUseCase {

    operator fun invoke(packages: List<Package>): List<Package> {
        if (packages.size <= MIN_PACKAGES_TO_SORT) {
            return packages
        }

        val indexed = packages
            .mapIndexed { index, pkg -> IndexedPackage(index, pkg) }
            .toMutableList()

        quickSortIndexed(indexed, startIndex = FIRST_INDEX, endIndex = indexed.size - INDEX_OFFSET)

        return indexed.map { it.pkg }
    }

    private data class IndexedPackage(
        val originalIndex: Int,
        val pkg: Package
    )

    private fun quickSortIndexed(
        list: MutableList<IndexedPackage>,
        startIndex: Int,
        endIndex: Int
    ) {
        if (startIndex < endIndex) {
            val pivotIndex = partitionIndexed(list, startIndex, endIndex, startIndex,
                startIndex - INDEX_OFFSET)

            quickSortIndexed(list, startIndex, pivotIndex - INDEX_OFFSET)

            quickSortIndexed(list, pivotIndex + INDEX_OFFSET, endIndex) }
    }

    private tailrec fun partitionIndexed(
        list: MutableList<IndexedPackage>,
        startIndex: Int,
        endIndex: Int,
        currentIndex: Int,
        boundaryIndex: Int
    ): Int {
        if (currentIndex >= endIndex) {
            swapIndexed(
                list,
                boundaryIndex + INDEX_OFFSET,
                endIndex
            )
            return boundaryIndex + INDEX_OFFSET
        }

        val nextBoundary =
            if (comesBeforeOrEqual(list[currentIndex], list[endIndex])) {
                swapIndexed(list, boundaryIndex + INDEX_OFFSET, currentIndex)
                boundaryIndex + INDEX_OFFSET
            } else { boundaryIndex
            }

        return partitionIndexed(list, startIndex, endIndex, currentIndex + INDEX_OFFSET, nextBoundary)
    }

    private fun comesBeforeOrEqual(
        a: IndexedPackage,
        b: IndexedPackage
    ): Boolean {
        val weightA = a.pkg.weight ?: DEFAULT_WEIGHT_FOR_MISSING_VALUE

        val weightB = b.pkg.weight ?: DEFAULT_WEIGHT_FOR_MISSING_VALUE

        return when {
            weightA != weightB -> weightA > weightB
            else -> a.originalIndex <= b.originalIndex
        }
    }

    private fun swapIndexed(
        list: MutableList<IndexedPackage>,
        firstIndex: Int,
        secondIndex: Int
    ) {
        if (firstIndex != secondIndex) {
            val temp = list[firstIndex]
            list[firstIndex] = list[secondIndex]
            list[secondIndex] = temp
        }
    }
}