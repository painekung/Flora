package com.example.flora.ScreenMain

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flora.Authentication.AuthViewModel
import com.example.flora.Firebase.Order
import com.example.flora.Firebase.OrderViewModel
import java.text.SimpleDateFormat
import java.util.*

// ── Palette ──
private val DeepViolet   = Color(0xFF3D0075)
private val RichPurple   = Color(0xFF6400B2)
private val BrightPurple = Color(0xFF820DDD)
private val SoftPurple   = Color(0xFFAB5DEB)
private val PaleLavender = Color(0xFFF0E8FF)
private val LightLav     = Color(0xFFF8F4FF)
private val CardBg       = Color(0xFFFFFFFF)
private val TextTitle    = Color(0xFF1A0A2E)
private val TextSub      = Color(0xFF6B5F80)
private val DivLine      = Color(0xFFEBE2F8)
private val SuccessGreen = Color(0xFF00B37D)
private val WarnAmber    = Color(0xFFF59E0B)
private val DangerRed    = Color(0xFFEF4444)

@Composable
fun OrderHistoryScreen(
    modifier: Modifier,
    navController: NavController,
    orderVM      : OrderViewModel = viewModel(factory = OrderViewModel.Factory),

) {
    val userEmail = viewModel<AuthViewModel>().currentUser?.email
    LaunchedEffect(Unit) { orderVM.loadOrderHistory(userEmail.toString()) }

    val orders     by orderVM.orderHistory.collectAsState()
    val isLoading  by orderVM.isHistoryLoading.collectAsState()
    var filterTab  by remember { mutableStateOf(0) } // 0=ทั้งหมด 1=success 2=pending

    val filteredOrders = remember(orders, filterTab) {
        when (filterTab) {
            1    -> orders.filter { it.status == "success" }
            2    -> orders.filter { it.status == "pending" }
            else -> orders
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightLav)
    ) {
        // ── decorative background blobs ──
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawDecorBlobs()
        }

        Column(modifier = Modifier.fillMaxSize()) {

            // ══════════════════════════════
            // HEADER
            // ══════════════════════════════
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(DeepViolet, RichPurple, BrightPurple),
                            start  = Offset(0f, 0f),
                            end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
            ) {
                // texture dots
                Canvas(modifier = Modifier.fillMaxSize()) {
                    repeat(6) { i ->
                        drawCircle(
                            color  = Color.White.copy(alpha = 0.04f + i * 0.01f),
                            radius = (60f + i * 40f) * density,
                            center = Offset(size.width * (0.8f + i * 0.1f), size.height * (0.2f - i * 0.05f))
                        )
                    }
                }

                // back button
                IconButton(
                    onClick  = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }

                // title block
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, bottom = 24.dp)
                ) {
                    Text(
                        text       = "ประวัติการสั่งซื้อ",
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text     = "${orders.size} รายการทั้งหมด",
                        fontSize = 13.sp,
                        color    = Color.White.copy(alpha = 0.7f)
                    )
                }

                // summary pill
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 24.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    val successCount = orders.count { it.status == "success" }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).background(SuccessGreen, CircleShape))
                        Text("$successCount สำเร็จ", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ══════════════════════════════
            // FILTER TABS
            // ══════════════════════════════
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-1).dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBg)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                listOf("ทั้งหมด", "สำเร็จ", "รอดำเนินการ").forEachIndexed { idx, label ->
                    val selected = filterTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selected) Brush.linearGradient(listOf(RichPurple, BrightPurple))
                                else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                            )
                            .clickable { filterTab = idx }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = label,
                            fontSize   = 13.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color      = if (selected) Color.White else TextSub
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ══════════════════════════════
            // CONTENT
            // ══════════════════════════════
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            CircularProgressIndicator(color = RichPurple, strokeWidth = 3.dp, modifier = Modifier.size(40.dp))
                            Text("กำลังโหลด...", color = TextSub, fontSize = 14.sp)
                        }
                    }
                }

                filteredOrders.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier         = Modifier.size(80.dp).background(PaleLavender, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ShoppingBag, null, tint = SoftPurple, modifier = Modifier.size(38.dp))
                            }
                            Text("ยังไม่มีประวัติการสั่งซื้อ", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextTitle)
                            Text("รายการสั่งซื้อจะแสดงที่นี่", fontSize = 13.sp, color = TextSub)
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier       = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        itemsIndexed(filteredOrders, key = { _, o -> o.orderID }) { index, order ->
                            OrderCard(order = order, index = index)
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════
// ORDER CARD
// ══════════════════════════════════════════
@Composable
private fun OrderCard(order: Order, index: Int) {
    var expanded by remember { mutableStateOf(false) }

    val animDelay = index * 60
    val slideOffset by animateIntAsState(
        targetValue = 0,
        animationSpec = tween(durationMillis = 400, delayMillis = animDelay, easing = EaseOutCubic),
        label = "slide"
    )

    val dateStr = order.createdAt?.let {
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("th")).format(it)
    } ?: "—"

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // ── gradient top strip ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        when (order.status) {
                            "success" -> Brush.linearGradient(listOf(SuccessGreen, Color(0xFF34D399)))
                            "pending" -> Brush.linearGradient(listOf(WarnAmber, Color(0xFFFBBF24)))
                            else      -> Brush.linearGradient(listOf(DangerRed, Color(0xFFF87171)))
                        }
                    )
            )

            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {

                // ── row 1: order id + status badge ──
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text       = "คำสั่งซื้อ #${order.orderID.takeLast(8).uppercase()}",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color      = TextTitle
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(dateStr, fontSize = 11.sp, color = TextSub)
                    }
                    StatusBadge(status = order.status)
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = DivLine, thickness = 1.dp)
                Spacer(Modifier.height(14.dp))

                // ── item summary pills ──
                val visibleItems = if (expanded) order.items else order.items.take(2)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    visibleItems.forEach { item ->
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {

                                Column {
                                    Text(
                                        text     = item.flowerName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color    = TextTitle,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text     = item.store.storeName.ifBlank { "ร้านค้า" },
                                        fontSize = 11.sp,
                                        color    = TextSub
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text       = "฿${item.price * item.quantity}",
                                    fontSize   = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = RichPurple
                                )
                                Text(
                                    text     = "x${item.quantity}",
                                    fontSize = 11.sp,
                                    color    = TextSub
                                )
                            }
                        }
                    }
                }

                // ── show more / less ──
                if (order.items.size > 2) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PaleLavender)
                            .clickable { expanded = !expanded }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text       = if (expanded) "ซ่อนรายการ" else "ดูเพิ่มอีก ${order.items.size - 2} รายการ",
                                fontSize   = 12.sp,
                                color      = RichPurple,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint     = RichPurple,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = DivLine, thickness = 1.dp)
                Spacer(Modifier.height(12.dp))

                // ── row 3: item count + total ──
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Inventory2, null, tint = TextSub, modifier = Modifier.size(14.dp))
                        Text("${order.items.size} รายการ", fontSize = 12.sp, color = TextSub)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("ยอดรวม", fontSize = 12.sp, color = TextSub)
                        Text(
                            text       = "฿${order.totalPrice}",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = RichPurple
                        )
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════
// STATUS BADGE
// ══════════════════════════════════════════
@Composable
private fun StatusBadge(status: String) {
    val (label, bgColor, textColor, dotColor) = when (status) {
        "success" -> Quadruple("สำเร็จ",         Color(0xFFDCFCE7), Color(0xFF15803D), SuccessGreen)
        "pending" -> Quadruple("รอดำเนินการ",    Color(0xFFFEF3C7), Color(0xFF92400E), WarnAmber)
        "cancel"  -> Quadruple("ยกเลิก",         Color(0xFFFEE2E2), Color(0xFF991B1B), DangerRed)
        else      -> Quadruple(status,            PaleLavender,      TextSub,           SoftPurple)
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // pulsing dot
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val alpha by infiniteTransition.animateFloat(
            initialValue   = 0.4f,
            targetValue    = 1f,
            animationSpec  = infiniteRepeatable(tween(800), RepeatMode.Reverse),
            label          = "alpha"
        )
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(dotColor.copy(alpha = if (status == "pending") alpha else 1f), CircleShape)
        )
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

// ── helper data class for destructuring ──
private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

// ── decorative background blobs ──
private fun DrawScope.drawDecorBlobs() {
    drawCircle(
        color  = Color(0xFF6400B2).copy(alpha = 0.04f),
        radius = 200.dp.toPx(),
        center = Offset(size.width * 1.1f, size.height * 0.15f)
    )
    drawCircle(
        color  = Color(0xFF820DDD).copy(alpha = 0.035f),
        radius = 160.dp.toPx(),
        center = Offset(-40.dp.toPx(), size.height * 0.55f)
    )
    drawCircle(
        color  = Color(0xFFAB5DEB).copy(alpha = 0.03f),
        radius = 120.dp.toPx(),
        center = Offset(size.width * 0.5f, size.height * 0.88f)
    )
}