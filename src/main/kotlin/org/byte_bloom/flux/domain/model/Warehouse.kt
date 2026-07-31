package org.byte_bloom.flux.domain.model

private const val DEFAULT_WEIGHT_FOR_MISSING_VALUE = 0.0
private const val MIN_PACKAGES_TO_SORT = 1
private const val FIRST_INDEX = 0
private const val INDEX_OFFSET = 1

data class Warehouse(
    val warehouseId: String,
    val warehouseName: String,
    val regionalZone: String,
    val cargoQueue: MutableList<Package> = mutableListOf(),
    val outgoingRoutes: MutableList<Route> = mutableListOf(),
    val stationedVehicles: MutableList<Vehicle> = mutableListOf()
) {

    fun sortCargoQueue() {

        val totalPackages = cargoQueue.size

        if (totalPackages <= MIN_PACKAGES_TO_SORT) return

        quickSortPackages(startIndex = FIRST_INDEX, endIndex = totalPackages - INDEX_OFFSET)
    }

    private fun quickSortPackages(startIndex: Int, endIndex: Int) {

        if (startIndex < endIndex) {

            val pivotIndex = partitionCargoByWeightDescending(
                startIndex = startIndex,
                endIndex = endIndex,
                currentIndex = startIndex,
                boundaryIndex = startIndex - INDEX_OFFSET
            )

            quickSortPackages(startIndex, pivotIndex - INDEX_OFFSET)
            quickSortPackages(pivotIndex + INDEX_OFFSET, endIndex)
        }
    }

    private tailrec fun partitionCargoByWeightDescending(
        startIndex: Int,
        endIndex: Int,
        currentIndex: Int,
        boundaryIndex: Int
    ): Int {

        if (currentIndex >= endIndex) {
            swapPackages(boundaryIndex + INDEX_OFFSET, endIndex)
            return boundaryIndex + INDEX_OFFSET
        }

        val currentWeight = cargoQueue[currentIndex].weight ?: DEFAULT_WEIGHT_FOR_MISSING_VALUE
        val pivotWeight = cargoQueue[endIndex].weight ?: DEFAULT_WEIGHT_FOR_MISSING_VALUE

        val nextBoundary = if (currentWeight >= pivotWeight) {
            swapPackages(boundaryIndex + INDEX_OFFSET, currentIndex)
            boundaryIndex + INDEX_OFFSET
        } else {
            boundaryIndex
        }

        return partitionCargoByWeightDescending(
            startIndex = startIndex,
            endIndex = endIndex,
            currentIndex = currentIndex + INDEX_OFFSET,
            boundaryIndex = nextBoundary
        )
    }

    private fun swapPackages(firstIndex: Int, secondIndex: Int) {

        if (firstIndex != secondIndex) {
            val temporaryPackageHolder = cargoQueue[firstIndex]
            cargoQueue[firstIndex] = cargoQueue[secondIndex]
            cargoQueue[secondIndex] = temporaryPackageHolder
        }
    }
}
