package logic.sorters

import org.byte_bloom.flux.dataholders.Package
import org.byte_bloom.flux.dataholders.Priority


class SelectionSort {

    fun sort(packages: List<Package>): List<Package> {
        val mutablePackages = packages.toMutableList()
        val packageListSize = mutablePackages.size

        for( i in 0 until (packageListSize-1)) {
            var maxIndex = i

            for (j in i + 1  until packageListSize) {
                if( isSconedHigher(mutablePackages[maxIndex],mutablePackages[j])){
                    maxIndex = j
                }
            }

            //now ew have the index of the max
            if (maxIndex != i) {
                val maxPackage = mutablePackages[maxIndex]
                for( k in maxIndex downTo i+1) {
                    mutablePackages[k] = mutablePackages[k-1]
                }
                mutablePackages[i] = maxPackage
            }
        }
        return mutablePackages
    }

    private fun isSconedHigher(first : Package , second : Package) : Boolean {
        //first is the max , second is اللي ممكن يتشقلب مهاع
        // لذاك السوال هنا هل التاني اكبر من الاور؟

        //check porortiy
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
        return if (priority == Priority.URGENT) {
            3
        } else if (priority == Priority.STANDARD) {
            2
        } else {
            1
        }
    }
}