package edu.logiroute.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import org.byte_bloom.flux.domain.model.Package
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import org.byte_bloom.flux.domain.model.Priority.*
import edu.logiroute.ui.theme.*
import org.byte_bloom.flux.domain.model.Warehouse

@Composable
fun PackagePriorityBadge(
    packageItem: Package,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (packageItem.priority) {
        URGENT -> ByteBloomErrorRed to ByteBloomInkBlack
        STANDARD -> ByteBloomLightGreen to ByteBloomInkBlack
        LOW -> ByteBloomCharcoalBlue to ByteBloomTextSecondary
    }
    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${packageItem.id}   ${packageItem.priority.name.uppercase()}",
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            packageItem.weight?.let { weight ->
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "  ${weight} kg",
                    color = ByteBloomTextSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

