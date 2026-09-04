package org.byte_bloom.flux.domain.response

import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Warehouse

data class SplitRerouteResult(
    val reroutedPackages: List<Package>,
    val updatedRoutePlans: Map<String, List<Warehouse>>
)
