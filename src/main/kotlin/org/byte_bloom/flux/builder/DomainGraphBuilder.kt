package org.byte_bloom.flux.builder

import org.byte_bloom.flux.domain.model.*

class DomainGraphBuilder {

    /**
     * يقوم ببناء الرسم البياني (Graph) بربط الكائنات ببعضها البعض.
     * تستخدم هذه الدالة الـ HashMaps لضمان تعقيد زمني خطي O(N).
     */
    fun buildGraph(
        warehouses: List<Warehouse>,
        packages: List<Package>,
        routes: List<Route>,
        vehicles: List<Vehicle>
    ): List<Warehouse> {

        // 1. فهرسة المخازن (Indexing) - تحويل القائمة إلى Map لسرعة الوصول O(1)
        val warehouseMap = warehouses.associateBy { it.warehouseId }

        // 2. ربط الشحنات (Packages) بالمخازن بناءً على الوجهة
        packages.forEach { pkg ->
            // نستخدم ?. للآمان في حال كان الـ ID غير موجود بالخطأ
            warehouseMap[pkg.destinationHubId]?.cargoQueue?.add(pkg)
        }

        // 3. ربط المركبات (Vehicles) بالمخازن بناءً على موقعها الحالي
        vehicles.forEach { vehicle ->
            warehouseMap[vehicle.currentHub]?.stationedVehicles?.add(vehicle)
        }

        // 4. ربط المسارات (Routes) بالمخازن بناءً على نقطة الانطلاق
        routes.forEach { route ->
            warehouseMap[route.originHub]?.outgoingRoutes?.add(route)
        }

        // نرجع قائمة المخازن، وهي الآن تحتوي على كل العلاقات المترابطة (Graph)
        return warehouses
    }
}