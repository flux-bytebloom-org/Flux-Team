package edu.logiroute.ui.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.logiroute.ui.component.PackagePriorityBadge
import edu.logiroute.ui.component.WarehouseSummaryCard
import edu.logiroute.ui.theme.*
import org.byte_bloom.flux.domain.model.Package
import org.byte_bloom.flux.domain.model.Priority.*
import org.byte_bloom.flux.domain.model.Vehicle
import org.byte_bloom.flux.domain.model.Warehouse


private val mockWarehouseNorth = Warehouse(
    id = "WH-001",
    name = "Central Logistics Hub",
    regionalZone = "NORTH",
    latitude = 37.91,
    longitude = -88.46
).apply {
    addPackage(Package("PKG-000009", 460.06, this, this, URGENT))
    addPackage(Package("PKG-000011", 283.33, this, this, STANDARD))
    addVehicle(Vehicle("VH-101", this, 1500.0, 2.5))
}

private val mockWarehouseSouth = Warehouse(
    id = "WH-028",
    name = "South Terminal",
    regionalZone = "SOUTH",
    latitude = 31.50,
    longitude = 34.46
)

private val urgentPkg = Package("PKG-000009", 460.06, mockWarehouseNorth, mockWarehouseNorth, URGENT)
private val standardPkg = Package("PKG-000011", 283.33, mockWarehouseNorth, mockWarehouseNorth, STANDARD)
private val lowPkg = Package("PKG-000012", null, mockWarehouseNorth, mockWarehouseNorth, LOW)

@Preview
@Composable
fun PackagePriorityBadgeAllStatesPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ByteBloomOnPrimary
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Package Priority Badges",
                color = ByteBloomTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            PackagePriorityBadge(packageItem = urgentPkg, modifier = Modifier.fillMaxWidth())
            PackagePriorityBadge(packageItem = standardPkg, modifier = Modifier.fillMaxWidth())
            PackagePriorityBadge(packageItem = lowPkg, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview
@Composable
fun WarehouseSummaryCardActivePreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ByteBloomOnPrimary
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Active Warehouse Summary",
                color = ByteBloomTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            WarehouseSummaryCard(
                warehouse = mockWarehouseNorth,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun WarehouseSummaryCardEmptyPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ByteBloomOnPrimary
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Empty Warehouse Summary",
                color = ByteBloomTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            WarehouseSummaryCard(
                warehouse = mockWarehouseSouth,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun AllWarehouseComponentsFullPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ByteBloomOnPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PackagePriorityBadgeAllStatesPreview()
            WarehouseSummaryCardActivePreview()
            WarehouseSummaryCardEmptyPreview()
        }
    }
}