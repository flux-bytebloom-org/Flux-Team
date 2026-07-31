package org.byte_bloom.flux.domain.model

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
        if (totalPackages <= 1) return

        quickSortPackages(startIndex = 0, endIndex = totalPackages - 1)
    }

    fun quickSortPackages(startIndex: Int, endIndex: Int) {
        if (startIndex < endIndex) {
            val pivotIndex = partitionCargoByWeightDescending(
                startIndex = startIndex,
                endIndex = endIndex,
                currentIndex = startIndex,
                boundaryIndex = startIndex - 1
            )

            quickSortPackages(startIndex, pivotIndex - 1)
            quickSortPackages(pivotIndex + 1, endIndex)
        }
    }

    private fun partitionCargoByWeightDescending(
        startIndex: Int,
        endIndex: Int,
        currentIndex: Int,
        boundaryIndex: Int
    ): Int {
        if (currentIndex >= endIndex) {
            swapPackages(boundaryIndex + 1, endIndex)
            return boundaryIndex + 1
        }

        val nextBoundary = if ((cargoQueue[currentIndex].weight ?: 0.0) >= (cargoQueue[endIndex].weight ?: 0.0)) {
            swapPackages(boundaryIndex + 1, currentIndex)
            boundaryIndex + 1
        } else {
            boundaryIndex
        }

        return partitionCargoByWeightDescending(
            startIndex = startIndex,
            endIndex = endIndex,
            currentIndex = currentIndex + 1,
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
