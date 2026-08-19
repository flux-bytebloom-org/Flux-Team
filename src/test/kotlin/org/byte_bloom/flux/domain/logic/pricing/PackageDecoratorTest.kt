package org.byte_bloom.flux.domain.logic.pricing

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.Warehouse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PackageDecoratorTest {

    @Test
    fun `decorators should stack fees dynamically`() {

        val warehouse = Warehouse(
            id = "W1",
            name = "Test Warehouse",
            regionalZone = "Zone A",
            latitude = 0.0,
            longitude = 0.0
        )

        val packageComponent: PackageComponent = Package(
            id = "P1",
            weight = 10.0,
            originHub = warehouse,
            destinationHub = warehouse,
            priority = Priority.STANDARD
        )

        var decoratedPackage: PackageComponent = packageComponent

        decoratedPackage = FragileHandlingDecorator(decoratedPackage)
        decoratedPackage = ColdChainDecorator(decoratedPackage)
        decoratedPackage = ExpressInsuranceDecorator(decoratedPackage)

        val result = decoratedPackage.calculateTransitRate(100.0)

        assertEquals(212.5, result)
    }
}
