package edu.logiroute.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.byte_bloom.flux.domain.model.Warehouse
import androidx.compose.material3.Surface
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import edu.logiroute.ui.theme.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text


@Composable
fun WarehouseIdentityBadge(
    warehouse: Warehouse,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = ByteBloomCharcoalBlue,
        shape = RoundedCornerShape(8.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Text(
                    text = warehouse.name,
                    color = ByteBloomTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = warehouse.id,
                    color = ByteBloomTextSecondary,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            warehouse.regionalZone?.let { zone ->
                if (zone.isNotBlank()) {
                    Spacer(modifier = Modifier.width(12.dp))

                    val (tagBgColor, tagTextColor) = when (zone.uppercase()) {
                        "NORTH", "WEST" -> ByteBloomSapphireSky to ByteBloomTextPrimary
                        "CENTRAL", "SOUTH", "EAST" -> ByteBloomCyberSprout to ByteBloomInkBlack
                        else -> ByteBloomSapphireSky to ByteBloomTextPrimary
                    }
                    Surface(
                        color = tagBgColor,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = zone.uppercase(),
                            color = tagTextColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

        }
    }
}
