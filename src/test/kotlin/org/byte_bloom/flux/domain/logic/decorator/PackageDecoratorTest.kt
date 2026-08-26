package org.byte_bloom.flux.domain.logic.decorator

import org.byte_bloom.flux.domain.logic.pricing.decorator.ColdChainDecorator
import org.byte_bloom.flux.domain.logic.pricing.decorator.ExpressInsuranceDecorator
import org.byte_bloom.flux.domain.logic.pricing.decorator.FragileHandlingDecorator
import org.byte_bloom.flux.domain.logic.pricing.decorator.PackageComponent
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority
import org.byte_bloom.flux.domain.model.Warehouse

class PackageDecoratorTest {

    fun shouldStackFeesDynamically() {
        val warehouse = createWarehouse()
        val basePackage: PackageComponent = createBasePackage(warehouse)

        var decoratedPackage: PackageComponent = basePackage
        decoratedPackage = FragileHandlingDecorator(decoratedPackage)
        decoratedPackage = ColdChainDecorator(decoratedPackage)
        decoratedPackage = ExpressInsuranceDecorator(decoratedPackage)

        val result = decoratedPackage.calculateTransitRate(BASE_DISTANCE)

        assertThat(result == EXPECTED_TOTAL_RATE, "Dynamic fee stacking calculation mismatch")
    }

    private fun createWarehouse(): Warehouse {
        return Warehouse(
            id = WAREHOUSE_ID,
            name = "Test Warehouse",
            regionalZone = "Zone A",
            latitude = DEFAULT_COORDINATE,
            longitude = DEFAULT_COORDINATE
        )
    }

    private fun createBasePackage(warehouse: Warehouse): PackageComponent {
        return Package(
            id = PACKAGE_ID,
            weight = PACKAGE_WEIGHT,
            originHub = warehouse,
            destinationHub = warehouse,
            priority = Priority.STANDARD
        )
    }

    private fun assertThat(condition: Boolean, message: String) {
        if (!condition) {
            throw AssertionError("Test Failed: $message")
        }
    }

    companion object {
        private const val WAREHOUSE_ID = "W1"
        private const val PACKAGE_ID = "P1"
        private const val PACKAGE_WEIGHT = 10.0
        private const val BASE_DISTANCE = 100.0
        private const val EXPECTED_TOTAL_RATE = 212.5
        private const val DEFAULT_COORDINATE = 0.0
    }
}

fun main() {
    val runner = PackageDecoratorTest()
    println("Running PackageDecoratorTest...")

    runner.shouldStackFeesDynamically()

    println("All PackageDecorator tests passed successfully!")
}
