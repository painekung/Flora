package com.example.flora.ScreenMain

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flora.R
import com.example.flora.viewmodels.FlowerViewModel


@Composable
fun WaveShape(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFEDE8F5)
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, height * 0.85f)

            cubicTo(
                width * 0.1f, height * 1.00f,
                width * 0.75f, height * 0.80f,
                width, height * 0.90f
            )

            lineTo(width, 0f)
            close()
        }
        drawPath(path = path, color = color)
    }
}

@Composable
fun FlowerDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    flowerViewModel: FlowerViewModel,
    shadowHeight: Dp = 20.dp,
    shadowAlpha: Float = 0.05f,
    shadowColor: Color = Color.Black
) {
    val flower = flowerViewModel.selectedFlower ?: return

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        val shadowHeightPx = shadowHeight.toPx()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    shadowColor.copy(alpha = shadowAlpha),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = -shadowHeightPx
                            ),
                            topLeft = Offset(0f, -shadowHeightPx),
                            size = size.copy(height = shadowHeightPx)
                        )
                    }
                    .background(Color.White)
                    .padding(16.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$${flower.price}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Button(
                    onClick = { },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E7CC3))
                ) {
                    Text("เพิ่มลงตะกร้า", color = Color.White, fontSize = 20.sp, modifier = Modifier.padding(4.dp))
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding())
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(379.dp)
                ) {
                    WaveShape(modifier = Modifier.fillMaxSize(), color = Color(0xFFE6E1F3))
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(55.dp))
                        Image(
                            painter = if (flower.image != null) painterResource(id = flower.image) else painterResource(id = R.drawable.fl),
                            contentDescription = null,
                            modifier = Modifier.size(324.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(35.dp))
                    }
                    IconButton(
                        onClick = { },
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(35.dp))
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(flower.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107))
                            Text(" ${flower.rating}", color = Color(0xFF8E889D))
                        }
                    }

                    Spacer(modifier = Modifier.height(27.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_background),
                                contentDescription = null,
                                modifier = Modifier.size(50.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Matsumoto Momona", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Momo", color = Color(0xFF8E889D), fontSize = 16.sp)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                            IconButton(
                                onClick = { },
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF8E7CC3))
                            ) {
                                Icon(Icons.Default.ChatBubble, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            IconButton(
                                onClick = { },
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF8E7CC3))
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(27.dp))
                    Text("รายละเอียด", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(flower.description, color = Color.Gray, lineHeight = 22.sp)

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
