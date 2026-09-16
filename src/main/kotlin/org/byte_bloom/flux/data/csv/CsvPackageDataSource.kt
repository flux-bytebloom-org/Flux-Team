package org.byte_bloom.flux.data.csv.datasource

/* ===============================================================
Note: if you need to change in warehouse file call its interface
* example
        class CsvPackageDataSource(
            private val pkgFilePath: String,
 ===>       private val warehouseDataSource: WarehouseDataSource
        ) : PackageDataSource {
            override fun updateOriginHub(packageId: String, hubId: String) {
                // edit if pkg file
 ===>           warehouseDataSource.addToHubQueue(packageId, hubId)
            }
}  ===============================================================*/

