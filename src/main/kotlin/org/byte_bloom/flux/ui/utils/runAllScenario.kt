package org.byte_bloom.flux.ui.utils

import org.byte_bloom.flux.ui.scenarios.runBottleneckCheckScenario
import org.byte_bloom.flux.ui.scenarios.runDispatchScenario
import org.byte_bloom.flux.ui.scenarios.runStandaloneUseCaseDemos
import org.byte_bloom.flux.domain.model.Warehouse
import org.byte_bloom.flux.domain.model.Package

fun runAllScenarios(warehousesGraph: List<Warehouse>, packages: List<Package>) {
    val bottleneckResult = runBottleneckCheckScenario(warehousesGraph, packages)
    printBottleneckReport(bottleneckResult)
    // val settledPackages = bottleneckResult.finalPackages

    val dispatchHub = warehousesGraph.firstOrNull { it.getCargoQueue().isNotEmpty() }
    val dispatchDestination = warehousesGraph.lastOrNull { it.id != dispatchHub?.id }
    val dispatchPackage = dispatchHub?.getCargoQueue()?.firstOrNull()

    if (dispatchHub != null && dispatchDestination != null && dispatchPackage != null) {
        runDispatchScenario(
            hub = dispatchHub,
            destination = dispatchDestination,
            pkg = dispatchPackage,
            tripPackages = dispatchHub.getCargoQueue()
        )
    } else {
        println("\n[SKIP] Dispatch scenario — not enough data (hub/destination/package) found.")
    }

    runStandaloneUseCaseDemos(warehousesGraph)
}
