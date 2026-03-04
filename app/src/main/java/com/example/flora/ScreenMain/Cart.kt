package com.example.flora.ScreenMain

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flora.R
import com.example.flora.ScreenMain.CardComponents.CartShop
import com.example.flora.models.Flower
import com.example.flora.models.Store

@Composable
fun CartScreen(modifier: Modifier = Modifier, navController: NavController) {

    // 1. เปลี่ยนข้อมูลสินค้าให้เป็น MutableStateList เพื่อให้ Compose แจ้งเตือนเมื่อมีการลบ
    val cartItems = remember {
        mutableStateListOf(
            Flower(id = 1, name = "ดอกทิวลิป", rating = 4.9, price = 30.00, image = R.drawable.fl, color = "0xFF7B2FF7", category = "อบอุ่น", storeId = "S01"),
            Flower(id = 2, name = "ดอกกุหลาบ", rating = 4.8, price = 25.00, image = null, color = "0xFFE91E63", storeId = "S01"),
            Flower(id = 3, name = "ดอกลาเวนเดอร์", rating = 4.7, price = 35.00, image = R.drawable.fl, color = "0xFF9C27B0", storeId = "S02"),
            Flower(id = 4, name = "ดอกทอง", rating = 4.9, price = 40.00, image = null, color = "0xFFFFC107", storeId = "S02"),
            Flower(id = 5, name = "ดอกเดซี่", rating = 4.6, price = 20.00, image = R.drawable.fl, color = "0xFFFFFFFF", storeId = "S01"),
            Flower(id = 6, name = "ดอกออร์คิด", rating = 4.9, price = 50.00, image = null, color = "0xFFFF4081", storeId = "S02"),
            Flower(id = 7, name = "ดอกซากุระ", rating = 5.0, price = 60.00, image = R.drawable.fl, color = "0xFFFFB7C5", storeId = "S01"),
            Flower(id = 8, name = "ดอกมะลิ", rating = 4.5, price = 15.00, image = null, color = "0xFFF5F5F5", storeId = "S02")
        )
    }

    val storeList = remember {
        listOf(
            Store(storeId = "S01", storeName = "ร้านดอกไม้ป้าใจ", location = "กรุงเทพ"),
            Store(storeId = "S02", storeName = "Garden Home", location = "เชียงใหม่"),
            Store(storeId = "S03", storeName = "Garden Shop", location = "ชลบุรี")
        )
    }

    val groupedByStore = cartItems.groupBy { it.storeId }

    LaunchedEffect(groupedByStore.size) {
        groupedByStore.forEach { (storeId, flowers) ->
            Log.d("g", "Store: $storeId → ${flowers.size} items")
            flowers.forEach { flower ->
                Log.d("g", "  - ${flower.name} | storeId: ${flower.storeId}")
            }
        }
    }

    // เก็บ ID สินค้าที่ถูกเลือก
    val selectedIds = remember { mutableStateListOf<Int>() }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // --- Header Section ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(30.dp), tint = Color(0xFF820DDD))
                }
                Text("รถเข็น (${cartItems.size})", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                Text("แก้ไขที่อยู่", color = Color(0xFF6E6A7C), fontSize = 14.sp)
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(28.dp), tint = Color(0xFF820DDD))
                }
            }
        }

        // --- Cart List Section ---
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // 1. จัดกลุ่มสินค้าเตรียมไว้ก่อน


            if (cartItems.isEmpty()) {
                item { /* แสดงข้อความตะกร้าว่าง */ }
            } else {
                // 2. ใช้ storeList เป็นหลักในการ Loop เพื่อล็อกลำดับร้านค้าให้คงที่
                // และกรองเอาเฉพาะร้านที่มีสินค้าอยู่ในตะกร้า (groupedByStore)
                val activeStores = storeList.filter { groupedByStore.containsKey(it.storeId) }

                items(
                    items = activeStores,
                    key = { it.storeId } // ล็อก Key ที่ระดับร้านค้า
                ) { storeInfo ->
                    // ดึงเฉพาะสินค้าของร้านนี้ออกมาแสดง
                    val flowersInStore = groupedByStore[storeInfo.storeId] ?: emptyList()
                    Spacer(modifier = Modifier.height(20.dp))
                    CartShop(
                        store = storeInfo,
                        flowers = flowersInStore,
                        selectedIds = selectedIds,
                        onRemoveFlower = { flowerToDelete ->
                            cartItems.remove(flowerToDelete)
                            selectedIds.remove(flowerToDelete.id)
                        }
                    )
                }
            }
        }
    }
}