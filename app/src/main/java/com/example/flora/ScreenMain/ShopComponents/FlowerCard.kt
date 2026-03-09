package com.example.flora.ScreenMain.ShopComponents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flora.Firebase.Flowers
import com.example.flora.ScreenMain.getFlowerResId

fun Modifier.coloredShadow(
    color       : Color = Color(0xFFE6E1F3),
    borderRadius: Dp    = 16.dp,
    blurRadius  : Dp    = 3.8.dp,
    offsetX     : Dp    = 0.dp,
    offsetY     : Dp    = 0.dp,
) = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                this.color = android.graphics.Color.TRANSPARENT
                setShadowLayer(blurRadius.toPx(), offsetX.toPx(), offsetY.toPx(), color.copy(alpha = .1f).toArgb())
            }
        }
        canvas.drawRoundRect(0f, 0f, size.width, size.height, borderRadius.toPx(), borderRadius.toPx(), paint)
    }
}

@Composable
fun FlowerCard(flower: Flowers, onClick: () -> Unit = {}) {
    val resId = remember(flower.name) { getFlowerResId(flower.name) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        onClick   = onClick,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {

            Card(
                modifier = Modifier.fillMaxWidth().height(160.dp),
                colors   = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model              = resId,
                        contentDescription = flower.name,
                        modifier           = Modifier.size(138.dp),
                        contentScale       = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text       = flower.name,
                    modifier   = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Text(" ${flower.rating}", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ราคา", color = Color(0xFF6E6A7C), fontSize = 12.sp)
                Text("฿${flower.price}", fontWeight = FontWeight.Bold)
            }
        }
    }
}