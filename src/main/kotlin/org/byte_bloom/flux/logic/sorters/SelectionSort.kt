package org.byte_bloom.flux.logic.sorters

import org.byte_bloom.flux.dataholders.Package
import org.byte_bloom.flux.dataholders.Priority


fun sortByPriorityAndWeightDescending(packages: List<Package>): List<Package> {
    val mutablePackages = packages.toMutableList()
    val packageListSize = mutablePackages.size

    for( currentIndex in 0 until (packageListSize-1)) {
        var maxIndex = currentIndex

        for (scanIndex in currentIndex + 1  until packageListSize) {
            if( hasHigherPriorityThenWeight(mutablePackages[scanIndex],mutablePackages[maxIndex])){
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


private fun hasHigherPriorityThenWeight(first : Package , second : Package) : Boolean {

    val firstPackagePriorityRank:Int = getPriorityRank(first.priority)
    val secondPackagePriorityRank:Int  = getPriorityRank(second.priority)

    if (firstPackagePriorityRank > secondPackagePriorityRank ) {
        return true
    }else if ( firstPackagePriorityRank == secondPackagePriorityRank){
        return (first.weight > second.weight)
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
