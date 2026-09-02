package org.byte_bloom.flux.domain.logic.tree

private const val NO_STEPS_TAKEN = 0
private const val STEP_INCREMENT = 1

class BinarySearchTree {
    private var root: BinarySearchTreeNode? = null

    fun insert(value: String) {
        root = insertNode(root, value)
    }

    private fun insertNode(node: BinarySearchTreeNode?, value: String): BinarySearchTreeNode {
        if (node == null) {
            return BinarySearchTreeNode(value)
        }

        if (value < node.value) {
            node.left = insertNode(node.left, value)
        } else if (value > node.value) {
            node.right = insertNode(node.right, value)
        }

        return node
    }

    fun searchStepCount(value: String): Int {
        return searchStepCount(root, value, stepsSoFar = NO_STEPS_TAKEN)
    }

    private fun searchStepCount(node: BinarySearchTreeNode?, value: String, stepsSoFar: Int): Int {
        if (node == null) {
            return stepsSoFar
        }

        val stepsAfterVisitingThisNode = stepsSoFar + STEP_INCREMENT

        return when {
            value == node.value -> stepsAfterVisitingThisNode
            value < node.value -> searchStepCount(node.left, value, stepsAfterVisitingThisNode)
            else -> searchStepCount(node.right, value, stepsAfterVisitingThisNode)
        }
    }
}
