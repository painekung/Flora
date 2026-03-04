package com.example.flora.ScreenMain.CardComponents

import android.widget.Space
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flora.R
import com.example.flora.models.Flower
import com.example.flora.models.Store
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.ui.text.style.TextAlign

@Composable
fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val backgroundColor = if (checked) Color(0xFF820DDD) else Color.Transparent
    val borderColor = if (checked) Color(0xFF820DDD) else Color(0xFFD1D1D1)

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartShop(
    store: Store?,
    flowers: List<Flower>,
    selectedIds: SnapshotStateList<Int>, // รับมาจากข้างนอกเพื่อให้ลบข้อมูลได้จริง
    onRemoveFlower: (Flower) -> Unit     // Callback เมื่อปัดทิ้ง
) {

    if (flowers.isEmpty()) return
    val flowerCounts = remember {
        mutableStateMapOf<Int, Int>().apply {
            flowers.forEach { put(it.id, it.count) }
        }
    }

    val isAllSelectedInStore = flowers.isNotEmpty() && flowers.all { selectedIds.contains(it.id) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // ส่วนชื่อร้าน
            Row(verticalAlignment = Alignment.CenterVertically) {
                CustomCheckbox(
                    checked = isAllSelectedInStore,
                    onCheckedChange = { checked ->
                        if (checked) {
                            flowers.forEach { if (!selectedIds.contains(it.id)) selectedIds.add(it.id) }
                        } else {
                            flowers.forEach { selectedIds.remove(it.id) }
                        }
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = store?.storeName ?: "ไม่พบชื่อร้าน",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }



            // ส่วนรายการสินค้า (ใช้ SwipeToDismissBox)
            flowers.forEach { flower ->
                key(flower.id) {
                    val currentCount = flowerCounts[flower.id] ?: 1
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                onRemoveFlower(flower)
                                true // ยืนยันการลบ
                            } else false
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false, // ปัดจากขวาไปซ้ายเพื่อลบเท่านั้น
                        backgroundContent = {
                            val color by animateColorAsState(
                                when (dismissState.targetValue) {
                                    SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.7f)
                                    else -> Color.Transparent
                                }, label = "delete_bg"
                            )
                            Box(
                                Modifier
                                    .fillMaxSize()

                                    .clip(RoundedCornerShape(20.dp))
                                    .background(color),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "ลบสินค้า",
                                    tint = Color.White,
                                    modifier = Modifier.padding(end = 20.dp)
                                )
                            }
                        }
                    ) {
                        // เนื้อหา Card สินค้าเดิมของคุณ
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White) // ต้องใส่พื้นหลังเพื่อให้ตอนปัดไม่เห็นเนื้อหาซ้อน
                                ,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomCheckbox(
                                checked = selectedIds.contains(flower.id),
                                onCheckedChange = { checked ->
                                    if (checked) selectedIds.add(flower.id) else selectedIds.remove(
                                        flower.id
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Row(
                                modifier = Modifier
                                    .weight(1f) // ใช้ weight แทนการ fillMaxWidth ใน Row ซ้อน Row
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFF3F1F6))
                                    .padding(16.dp)
                                    .height(100.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Card(
                                    modifier = Modifier.size(100.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(Color(0xFFEAE5F4))
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.fl),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(
                                    modifier = Modifier.fillMaxHeight(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(flower.name, fontWeight = FontWeight.Bold)



                                    Row(modifier = Modifier.fillMaxWidth()) {
                                       IconButton(
                                           modifier = Modifier.size(24.dp),
                                           onClick = {
                                           if (currentCount > 1) {
                                               flowerCounts[flower.id] = currentCount - 1
                                               flower.count = currentCount - 1 // อัปเดตกลับไปที่ Model ด้วย
                                           }
                                       }) {
                                           Icon(
                                               Icons.Default.RemoveCircle,
                                               modifier = Modifier.fillMaxSize(),
                                               contentDescription = "ลบสินค้า",
                                               tint = Color(0xFF820DDD),

                                           )
                                       }
                                        Text(currentCount.toString(), textAlign = TextAlign.Center, modifier = Modifier.background(Color.Transparent).width(35.dp), fontWeight = FontWeight.Bold, color = Color(0xFF820DDD))

                                        IconButton(
                                            modifier = Modifier.size(24.dp),
                                            onClick = {
                                                flowerCounts[flower.id] = currentCount + 1
                                                flower.count = currentCount + 1 // อัปเดตกลับไปที่ Model
                                            }
                                        ) {
                                            Icon(
                                                modifier = Modifier.fillMaxSize(),
                                                imageVector =  Icons.Default.AddCircle,
                                                contentDescription = "ลบสินค้า",
                                                tint = Color(0xFF820DDD),

                                            )
                                        }

                                    }
                                    Text(
                                        "฿${flower.price}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF820DDD)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}