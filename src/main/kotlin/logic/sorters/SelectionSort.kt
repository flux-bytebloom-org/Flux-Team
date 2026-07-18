package logic.sorters

import org.byte_bloom.flux.dataholders.Package
import org.byte_bloom.flux.dataholders.Priority

class SelectionSort {

    fun sort(packages: List<Package>): List<Package> {
        val mutablePackages = packages.toMutableList()
        val packageListSize = mutablePackages.size

        for( currentIndex in 0 until (packageListSize-1)) {
            var maxIndex = currentIndex

            for (scanIndex in currentIndex + 1  until packageListSize) {
                if( isSecondHigher(mutablePackages[maxIndex],mutablePackages[scanIndex])){
                    maxIndex = scanIndex
                }
            }

            //now we have the index of the max, so we will shift forwrd the others to make his space

            if (maxIndex != currentIndex) {
                val maxPackage = mutablePackages[maxIndex]
                for( shiftIndex in maxIndex downTo currentIndex+1) {
                    mutablePackages[shiftIndex] = mutablePackages[shiftIndex-1]
                }
                mutablePackages[currentIndex] = maxPackage
            }
        }
        return mutablePackages
    }


    private fun isSecondHigher(first : Package , second : Package) : Boolean {

        val firstPackegePrioriytyRank:Int = getPriorityRank(first.priority)
        val secondPackegePrioriytyRank:Int  = getPriorityRank(second.priority)

        if (secondPackegePrioriytyRank > firstPackegePrioriytyRank ) {
            return true
        }else if ( secondPackegePrioriytyRank == firstPackegePrioriytyRank){
            return (second.weight > first.weight)

        }
        return false
    }

    private fun getPriorityRank(priority: Priority): Int {
        return when (priority) {
            Priority.URGENT -> {
                3
            }
            Priority.STANDARD -> {
                2
            }
            else -> {
                1
            }
        }
    }
}