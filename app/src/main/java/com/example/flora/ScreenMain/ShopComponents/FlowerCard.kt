package com.example.flora.ScreenMain.ShopComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.example.flora.R
import com.example.flora.models.Flower

// ✅ ต้องมี id


fun Modifier.coloredShadow(
    color: Color = Color(0xFFE6E1F3),
    borderRadius: Dp = 16.dp,
    blurRadius: Dp = 3.8.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
) = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                this.color = android.graphics.Color.TRANSPARENT
                setShadowLayer(
                    blurRadius.toPx(),
                    offsetX.toPx(),
                    offsetY.toPx(),
                    color.copy(alpha = .1f).toArgb()
                )
            }
        }
        canvas.drawRoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = borderRadius.toPx(),
            radiusY = borderRadius.toPx(),
            paint = paint
        )
    }
}


@Composable
fun FlowerCard(flower: Flower,onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth().height(160.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
               Column(
                   modifier = Modifier.fillMaxSize(),
                   verticalArrangement = Arrangement.Center,
                   horizontalAlignment = Alignment.CenterHorizontally
               ) {
                   Image(
                       painter = painterResource(id = R.drawable.fl),
                       contentDescription = null,
                       modifier = Modifier.size(138.dp),
                       contentScale = ContentScale.Fit
                   )
               }
            }

            Spacer(modifier = Modifier.height(8.dp))

          Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
              Text(
                  text = flower.name,
                  modifier = Modifier
                      .weight(1f),
                  fontWeight = FontWeight.Bold,
                  fontSize = 20.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
              )
              Spacer(Modifier.width(10.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                      imageVector = Icons.Default.Star,
                      contentDescription = null,
                      tint = Color(0xFFFFC107),
                      modifier = Modifier.size(16.dp)
                  )
                  Text(" ${flower.rating}", fontSize = 13.sp)
              }
          }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ราคา", color = Color(0xFF6E6A7C))
                Text("$${flower.price}", fontWeight = FontWeight.Bold)
            }
        }
    }
}