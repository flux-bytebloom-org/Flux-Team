package org.byte_bloom.flux.domain.logic.tree

private const val MIDDLE_DIVISOR = 2
private const val NEXT_INDEX_OFFSET = 1

fun buildBalancedInsertionOrder(sortedValues: List<String>): List<String> {
    val insertionOrder = mutableListOf<String>()
    addMiddleFirst(sortedValues, sortedValues.indices, insertionOrder)
    return insertionOrder
}

private fun addMiddleFirst(
    sortedValues: List<String>,
    range: IntRange,
    insertionOrder: MutableList<String>
) {
    if (range.isEmpty()) {
        return
    }

    val middleIndex = (range.first + range.last) / MIDDLE_DIVISOR
    insertionOrder.add(sortedValues[middleIndex])

    val indexAfterMiddle = middleIndex + NEXT_INDEX_OFFSET

    addMiddleFirst(sortedValues, range.first until middleIndex, insertionOrder)
    addMiddleFirst(sortedValues, indexAfterMiddle..range.last, insertionOrder)
}
