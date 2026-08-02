package org.byte_bloom.flux.data.dataholders

data class VehicleRaw(
    val vehicleId: String,
    val currentHub: String,      // ربط مع كائن Warehouse
    val maxCapacityKg: Double,      // سعة التحميل بالكيلوجرام
    val costPerKm: Double           // التكلفة لكل كيلومتر
)
//currentHub to Warehouse
