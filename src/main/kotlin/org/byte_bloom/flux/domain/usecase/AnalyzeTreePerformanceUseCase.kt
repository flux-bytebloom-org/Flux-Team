package org.byte_bloom.flux.domain.usecase

import org.byte_bloom.flux.domain.logic.tree.BinarySearchTree
import org.byte_bloom.flux.domain.logic.tree.buildBalancedInsertionOrder

class AnalyzeTreePerformanceUseCase {
    operator fun invoke() {
        val trackingIds = mutableListOf<String>()
        for (number in 1..1000) {
            trackingIds.add("PKG-" + number.toString().padStart(6, '0'))
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
