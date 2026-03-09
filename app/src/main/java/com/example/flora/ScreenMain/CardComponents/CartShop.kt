package com.example.flora.ScreenMain.CardComponents

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flora.R
import com.example.flora.ScreenMain.getFlowerResId
import com.example.flora.models.Flower
import com.example.flora.models.Store

// ─────────────────────────────────────────
// Custom Checkbox
// ─────────────────────────────────────────
@Composable
fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val backgroundColor = if (checked) Color(0xFF820DDD) else Color.Transparent
    val borderColor     = if (checked) Color(0xFF820DDD) else Color(0xFFD1D1D1)

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

// ─────────────────────────────────────────
// CartShop — เชื่อมกับ OrderViewModel
// ─────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartShop(
    store            : Store?,
    flowers          : List<Flower>,
    selectedIds      : SnapshotStateList<Int>,
    onRemoveFlower   : (Flower) -> Unit,
    onQuantityChange : (Flower, Int) -> Unit = { _, _ -> }   // ← callback sync DB
) {
    if (flowers.isEmpty()) return

    // local quantity map (UI state)
    val flowerCounts = remember {
        mutableStateMapOf<Int, Int>().apply {
            flowers.forEach { put(it.id, it.count) }
        }
    }

    val isAllSelected = flowers.isNotEmpty() && flowers.all { selectedIds.contains(it.id) }

    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors    = CardDefaults.cardColors(Color.White),
        shape     = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        ) {

            // ── ชื่อร้าน + checkbox เลือกทั้งหมด ──
            Row(verticalAlignment = Alignment.CenterVertically) {
                CustomCheckbox(
                    checked = isAllSelected,
                    onCheckedChange = { checked ->
                        if (checked) flowers.forEach { if (!selectedIds.contains(it.id)) selectedIds.add(it.id) }
                        else flowers.forEach { selectedIds.remove(it.id) }
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(store?.storeName ?: "ไม่พบชื่อร้าน", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            // ── รายการสินค้า ──
            flowers.forEach { flower ->
                key(flower.id) {
                    val currentCount = flowerCounts[flower.id] ?: 1

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                onRemoveFlower(flower)
                                true
                            } else false
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
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
                                Icon(Icons.Default.Delete, "ลบ", tint = Color.White,
                                    modifier = Modifier.padding(end = 20.dp))
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomCheckbox(
                                checked = selectedIds.contains(flower.id),
                                onCheckedChange = { checked ->
                                    if (checked) selectedIds.add(flower.id)
                                    else selectedIds.remove(flower.id)
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFF3F1F6))
                                    .padding(12.dp)
                                    .height(70.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // รูปสินค้า
                                Card(
                                    modifier = Modifier.size(70.dp),
                                    shape    = RoundedCornerShape(20.dp),
                                    colors   = CardDefaults.cardColors(Color(0xFFEAE5F4))
                                ) {
                                    Image(
                                        painter = painterResource(id = getFlowerResId(flower.name)),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize().padding(10.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(
                                    modifier = Modifier.fillMaxHeight(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(flower.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                                    // ── ปุ่ม + / จำนวน / - ──
                                    Row(modifier = Modifier.fillMaxWidth()) {

                                        // ปุ่ม −
                                        IconButton(
                                            modifier = Modifier.size(24.dp),
                                            onClick  = {
                                                val newCount = (currentCount - 1).coerceAtLeast(1)
                                                flowerCounts[flower.id] = newCount
                                                flower.count = newCount
                                                onQuantityChange(flower, newCount)
                                            }
                                        ) {
                                            Icon(Icons.Default.RemoveCircle, "ลด",
                                                modifier = Modifier.fillMaxSize(),
                                                tint     = Color(0xFF820DDD))
                                        }

                                        // จำนวน
                                        Text(
                                            currentCount.toString(),
                                            textAlign  = TextAlign.Center,
                                            modifier   = Modifier.width(35.dp),
                                            fontWeight = FontWeight.Bold,
                                            color      = Color(0xFF820DDD)
                                        )

                                        // ปุ่ม +
                                        IconButton(
                                            modifier = Modifier.size(24.dp),
                                            onClick  = {
                                                val newCount = currentCount + 1
                                                flowerCounts[flower.id] = newCount
                                                flower.count = newCount
                                                onQuantityChange(flower, newCount)
                                            }
                                        ) {
                                            Icon(Icons.Default.AddCircle, "เพิ่ม",
                                                modifier = Modifier.fillMaxSize(),
                                                tint     = Color(0xFF820DDD))
                                        }
                                    }

                                    Text(
                                        "฿${flower.price}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color      = Color(0xFF820DDD)
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