package edu.logiroute.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.byte_bloom.flux.domain.model.Warehouse
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.logiroute.ui.theme.*
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment


@Composable
fun WarehouseSummaryCard(
    warehouse: Warehouse,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.border(
            width = 1.dp,
            color = ByteBloomBorder,
            shape = RoundedCornerShape(12.dp)
        ),
        color = ByteBloomCharcoalBlue,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WarehouseIdentityBadge(
                warehouse = warehouse,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Packages: ${warehouse.getCargoQueue().size}",
                    color = ByteBloomTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Vehicles: ${warehouse.getStationedVehicles().size}",
                    color = ByteBloomTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            if (warehouse.getCargoQueue().isNotEmpty()) {
                val topPackage = warehouse.getCargoQueue().first()

                PackagePriorityBadge(
                    packageItem = topPackage,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Surface(
                    color = ByteBloomJetBlack.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No Pending Cargo",
                        color = ByteBloomTextDisabled,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
                    )
                }
            }
        }
    }
}
