package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.logic.tree.BinarySearchTree
import org.byte_bloom.flux.domain.logic.tree.buildBalancedInsertionOrder

private const val TRACKING_ID_RANGE_START = 1
private const val TRACKING_ID_COUNT = 1000
private const val TRACKING_ID_NUMBER_WIDTH = 6
private const val TRACKING_ID_PADDING_CHAR = '0'

class AnalyzeTreePerformanceUseCase {
    operator fun invoke() {
        val trackingIds = mutableListOf<String>()
        for (number in TRACKING_ID_RANGE_START..TRACKING_ID_COUNT) {
            trackingIds.add("PKG-" + number.toString().padStart(TRACKING_ID_NUMBER_WIDTH, TRACKING_ID_PADDING_CHAR))
        }

        val unbalancedTree = BinarySearchTree()
        for (id in trackingIds) {
            unbalancedTree.insert(id)
        }

        val balancedInsertionOrder = buildBalancedInsertionOrder(trackingIds)
        val balancedTree = BinarySearchTree()
        for (id in balancedInsertionOrder) {
            balancedTree.insert(id)
        }

        val targetId = trackingIds.last()

        val unbalancedSteps = unbalancedTree.searchStepCount(targetId)
        val balancedSteps = balancedTree.searchStepCount(targetId)

        println("Searching for $targetId")
        println("Unbalanced BST steps: $unbalancedSteps")
        println("Balanced tree steps: $balancedSteps")
    }
}
