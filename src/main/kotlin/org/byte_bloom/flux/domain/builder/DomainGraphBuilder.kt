package org.byte_bloom.flux.domain.builder

import org.byte_bloom.flux.data.dataholders.Warehouse as RawWarehouse
import org.byte_bloom.flux.domain.model.Warehouse as DomainWarehouse


class DomainGraphBuilder {


    fun buildWarehouses(
        rawWarehouses: List<RawWarehouse>
    ): Map<String, DomainWarehouse> {


        val warehouses = rawWarehouses.map { raw ->

            DomainWarehouse(
                warehouseId = raw.warehouseId,
                name = raw.name,
                regionalZone = raw.regionalZone
            )
        }


        return warehouses.associateBy {
            it.warehouseId
        }
    }
}