package org.byte_bloom.flux.domain.usecase

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import org.byte_bloom.flux.domain.algorithm.tree.WarehouseTreeNode
import org.byte_bloom.flux.domain.testdata.aWarehouse

class TraceHubLineageUseCaseTest {

    private val useCase = TraceHubLineageUseCase()

    @Test
    fun `returns the leaf first and the root last`() {
        // Given: root -> depot -> leaf
        val root = WarehouseTreeNode(aWarehouse(id = "WH-ROOT"))
        val depot = WarehouseTreeNode(aWarehouse(id = "WH-DEPOT"))
        val leaf = WarehouseTreeNode(aWarehouse(id = "WH-LEAF"))
        root.addChild(depot)
        depot.addChild(leaf)

        // When
        val lineage = useCase(leaf)

        // Then
        assertThat(lineage.map { it.id })
            .containsExactly("WH-LEAF", "WH-DEPOT", "WH-ROOT")
            .inOrder()
    }

    @Test
    fun `returns only the root when the node is the root`() {
        // Given
        val root = WarehouseTreeNode(aWarehouse(id = "WH-ROOT"))

        // When
        val lineage = useCase(root)

        // Then
        assertThat(lineage.map { it.id }).containsExactly("WH-ROOT")
    }

    @Test
    fun `follows only the ancestors and ignores siblings and children`() {
        // Given: root has two depots, depotA has a child
        val root = WarehouseTreeNode(aWarehouse(id = "WH-ROOT"))
        val depotA = WarehouseTreeNode(aWarehouse(id = "WH-A"))
        val depotB = WarehouseTreeNode(aWarehouse(id = "WH-B"))
        val childOfA = WarehouseTreeNode(aWarehouse(id = "WH-A1"))
        root.addChild(depotA)
        root.addChild(depotB)
        depotA.addChild(childOfA)

        // When
        val lineage = useCase(depotA)

        // Then
        assertThat(lineage.map { it.id }).containsExactly("WH-A", "WH-ROOT").inOrder()
    }

}
