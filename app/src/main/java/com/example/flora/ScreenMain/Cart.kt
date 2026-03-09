package com.example.flora.ScreenMain

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flora.Authentication.AuthViewModel
import com.example.flora.Firebase.OrderState
import com.example.flora.Firebase.OrderViewModel
import com.example.flora.ScreenMain.CardComponents.CartShop
import com.example.flora.models.Flower
import com.example.flora.models.Store


@Composable
fun CartScreen(
    modifier     : Modifier = Modifier,
    navController: NavController,
    orderVM      : OrderViewModel = viewModel(factory = OrderViewModel.Factory)
) {

    val email = viewModel<AuthViewModel>().currentUser?.email

    LaunchedEffect(Unit) {
        Log.d("Test Order",email.toString())
        orderVM.loadCart(email.toString())
    }

    val dbCartItems  by orderVM.cartItems.collectAsState()
    val currentOrder by orderVM.currentOrder.collectAsState()
    val orderState   by orderVM.orderState.collectAsState()

    val isLoading    = orderState is OrderState.Loading

    // Snackbar สำหรับ error
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(orderState) {
        if (orderState is OrderState.Error) {
            snackbarHostState.showSnackbar("❌ ${(orderState as OrderState.Error).message}")
            orderVM.resetOrderState()
        }
    }

    LaunchedEffect(dbCartItems) {
        Log.d("CartDebug", "cartItems size = ${dbCartItems.size}")
    }

    // แปลง OrderItem → Flower
    val cartItems = remember(dbCartItems) {
        dbCartItems.map { orderItem ->
            Flower(
                id      = orderItem.flowerID.hashCode(),
                name    = orderItem.flowerName,
                rating  = 0.0,
                price   = orderItem.price.toDouble(),
                image   = null,
                color   = "",
                storeId = orderItem.store.storeID,
                count   = orderItem.quantity,
                tag     = orderItem.flowerID
            )
        }.toMutableStateList()
    }

    // สร้าง storeList จาก embedded store
    val storeList = remember(dbCartItems) {
        dbCartItems
            .map { orderItem ->
                Store(
                    storeId   = orderItem.store.storeID,
                    storeName = orderItem.store.storeName,
                    location  = orderItem.store.location
                )
            }
            .distinctBy { it.storeId }
    }

    val groupedByStore = cartItems.groupBy { it.storeId }
    val selectedIds    = remember { mutableStateListOf<Int>() }

    val totalPrice by remember(dbCartItems, selectedIds.toList()) {
        derivedStateOf {
            dbCartItems
                .filter { orderItem -> selectedIds.contains(orderItem.flowerID.hashCode()) }
                .sumOf { it.price * it.quantity }
        }
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // แสดง bottomBar เฉพาะเมื่อมีสินค้าใน cart
            AnimatedVisibility(
                visible = cartItems.isNotEmpty(),
                enter   = slideInVertically(initialOffsetY = { it }),
                exit    = slideOutVertically(targetOffsetY = { it })
            ) {
                Surface(tonalElevation = 8.dp, color = Color.White) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("เลือก ${selectedIds.size} รายการ", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                if (selectedIds.isEmpty()) "฿0" else "฿$totalPrice",
                                fontSize   = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Color(0xFF6400B2)
                            )
                        }
                        Button(
                            onClick = {
                                // ── [NEW] เรียก checkout เมื่อกดสั่งซื้อ ──
                                orderVM.checkoutOrder()
                            },
                            enabled  = selectedIds.isNotEmpty() && !isLoading,
                            shape    = RoundedCornerShape(50.dp),
                            colors   = ButtonDefaults.buttonColors(
                                containerColor         = Color(0xFF820DDD),
                                disabledContainerColor = Color(0xFFCCCCCC)
                            ),
                            modifier = Modifier.height(48.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color       = Color.White,
                                    modifier    = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.ShoppingCartCheckout, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("สั่งซื้อ", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            // Header
            Row(
                modifier              = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(30.dp), tint = Color(0xFF820DDD))
                    }
                    Text("รถเข็น (${cartItems.size})", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                    Text("แก้ไขที่อยู่", color = Color(0xFF6E6A7C), fontSize = 14.sp)
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Message, null, modifier = Modifier.size(28.dp), tint = Color(0xFF820DDD))
                    }
                }
            }

            // Cart List
            LazyColumn(
                modifier       = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                when {
                    // ── [NEW] หลัง checkout สำเร็จ → แสดง empty state ──
                    cartItems.isEmpty() && orderState is OrderState.Checkout -> {
                        item {
                            Box(
                                modifier         = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier         = Modifier
                                            .size(80.dp)
                                            .background(Color(0xFF6400B2).copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint     = Color(0xFF6400B2),
                                            modifier = Modifier.size(44.dp)
                                        )
                                    }
                                    Text(
                                        "ชำระเงินสำเร็จ!",
                                        fontSize   = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = Color(0xFF1A1A2E)
                                    )
                                    Text(
                                        "ขอบคุณที่ใช้บริการ",
                                        fontSize = 14.sp,
                                        color    = Color.Gray
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Button(
                                        onClick = { navController.popBackStack() },
                                        shape   = RoundedCornerShape(50.dp),
                                        colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF820DDD))
                                    ) {
                                        Text("กลับหน้าหลัก", fontSize = 15.sp)
                                    }
                                }
                            }
                        }
                    }

                    // ── ตะกร้าว่าง (ยังไม่เคย checkout) ──
                    cartItems.isEmpty() -> {
                        item {
                            Box(
                                modifier         = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("ตะกร้าว่างเปล่า 🛒", color = Color.Gray, fontSize = 16.sp)
                            }
                        }
                    }

                    // ── มีสินค้าใน cart (status = pending) ──
                    else -> {
                        val activeStores = storeList.filter { groupedByStore.containsKey(it.storeId) }
                        items(items = activeStores, key = { it.storeId }) { storeInfo ->
                            val flowersInStore = groupedByStore[storeInfo.storeId] ?: emptyList()
                            Spacer(modifier = Modifier.height(20.dp))
                            CartShop(
                                store          = storeInfo,
                                flowers        = flowersInStore,
                                selectedIds    = selectedIds,
                                onRemoveFlower = { flowerToDelete ->
                                    cartItems.remove(flowerToDelete)
                                    selectedIds.remove(flowerToDelete.id)
                                    val realFlowerID = flowerToDelete.tag ?: flowerToDelete.id.toString()
                                    orderVM.removeItem(realFlowerID)
                                },
                                onQuantityChange = { flower, newQty ->
                                    val realFlowerID = flower.tag ?: flower.id.toString()
                                    orderVM.updateQuantity(flowerID = realFlowerID, newQty = newQty)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}