package com.example.flora.ScreenMain

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.flora.Authentication.AuthViewModel
import com.example.flora.Firebase.Flowers
import com.example.flora.Firebase.FlowersViewModel
import com.example.flora.Firebase.OrderState
import com.example.flora.Firebase.OrderViewModel
import com.example.flora.Firebase.Stores


fun String.toComposeColor(fallback: Color = Color(0xFFE6E1F3)): Color {
    return try {
        val hex = if (this.startsWith("#")) this else "#$this"
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) { fallback }
}

@Composable
fun FlowerDetailScreen(
    modifier       : Modifier,
    navController  : NavController,
    flowerViewModel: FlowersViewModel,
    orderVM        : OrderViewModel = viewModel(factory = OrderViewModel.Factory)
) {
    val flower by flowerViewModel.selectedFlower.collectAsState()
    val store  by flowerViewModel.selectedStore.collectAsState()
    val email = viewModel<AuthViewModel>().currentUser?.email

    if (flower == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val item = flower!!

    val waveColor      = remember(item.color)         { item.color.toComposeColor(fallback = Color(0xFFE6E1F3)) }
    val btnColor       = remember(item.colorBtn)      { item.colorBtn.toComposeColor(fallback = Color(0xFF8E7CC3)) }
    val storeIconColor = remember(item.categoryColor) { item.categoryColor.toComposeColor(fallback = Color(0xFFE6E1F3)) }

    val orderState        by orderVM.orderState.collectAsState()
    val snackbarHostState  = remember { SnackbarHostState() }

    LaunchedEffect(orderState) {
        when (orderState) {
            is OrderState.Success -> snackbarHostState.showSnackbar("🛒 เพิ่ม ${item.name} ลงตะกร้าแล้ว!")
            is OrderState.Error   -> snackbarHostState.showSnackbar("❌ ${(orderState as OrderState.Error).message}")
            else -> Unit
        }
        orderVM.resetOrderState()
    }

    LaunchedEffect(item) { Log.d("st", item.toString()) }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(snackbarData = data, containerColor = Color(0xFF1E1E2E), contentColor = Color.White, shape = RoundedCornerShape(14.dp))
            }
        },
        bottomBar = {
            BottomBuyBar(
                price       = item.price,
                btnColor    = btnColor,
                isLoading   = orderState is OrderState.Loading,
                onAddToCart = { orderVM.addToCart(flower = item, email = email.toString())}
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier       = Modifier.fillMaxSize().background(Color.White),
            contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding())
        ) {
            item(key = "detail_header") {
                DetailHeader(item = item, waveColor = waveColor) { navController.popBackStack() }
            }
            item(key = "detail_body") {
                Column(modifier = Modifier.padding(20.dp)) {
                    FlowerTitleRow(name = item.name, rating = item.rating)
                    Spacer(modifier = Modifier.height(24.dp))
                    StoreInfoSection(store = store, accentColor = storeIconColor, btnColor = btnColor)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("รายละเอียด", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(item.desc, color = Color.Gray, lineHeight = 24.sp, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun BottomBuyBar(
    price      : Int,
    btnColor   : Color,
    isLoading  : Boolean = false,
    onAddToCart: () -> Unit
) {
    val priceColor = remember(btnColor) {
        btnColor.copy(
            red   = (btnColor.red   * 0.75f).coerceIn(0f, 1f),
            green = (btnColor.green * 0.75f).coerceIn(0f, 1f),
            blue  = (btnColor.blue  * 0.75f).coerceIn(0f, 1f)
        )
    }
    Row(
        modifier              = Modifier.fillMaxWidth().height(72.dp).padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text("฿$price", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = priceColor)
        Button(
            onClick = onAddToCart,
            enabled = !isLoading,
            shape   = RoundedCornerShape(50.dp),
            colors  = ButtonDefaults.buttonColors(containerColor = btnColor, disabledContainerColor = btnColor.copy(alpha = 0.5f))
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("เพิ่มลงตะกร้า", color = Color.White, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
    }
}

@Composable
fun StoreInfoSection(store: Stores?, accentColor: Color = Color(0xFFE6E1F3), btnColor: Color = Color(0xFF8E7CC3)) {
    val iconTint = remember(accentColor) {
        accentColor.copy(
            red   = (accentColor.red   * 0.55f).coerceIn(0f, 1f),
            green = (accentColor.green * 0.55f).coerceIn(0f, 1f),
            blue  = (accentColor.blue  * 0.55f).coerceIn(0f, 1f)
        )
    }
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(accentColor), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Store, null, tint = iconTint)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(store?.storeName ?: "กำลังโหลดข้อมูลร้าน...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(store?.location  ?: "ไม่ระบุตำแหน่ง", color = Color.Gray, fontSize = 14.sp)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilledIconButton(onClick = {}, colors = IconButtonDefaults.filledIconButtonColors(containerColor = btnColor)) {
                Icon(Icons.Default.ChatBubble, null, modifier = Modifier.size(18.dp), tint = Color.White)
            }
            FilledIconButton(onClick = {}, colors = IconButtonDefaults.filledIconButtonColors(containerColor = btnColor)) {
                Icon(Icons.Default.Phone, null, modifier = Modifier.size(18.dp), tint = Color.White)
            }
        }
    }
}

@Composable
fun FlowerTitleRow(name: String, rating: Double) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107))
            Text(" $rating", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

fun getFlowerResId(flowerName: String): Int {
    return when (flowerName) {
        "ดอกลาเวนเดอร์มิกซ์" -> com.example.flora.R.drawable.fl
        "ดอกกุหลาบฟ้า"        -> com.example.flora.R.drawable.fl1
        "ดอกทานตะวันทอง"      -> com.example.flora.R.drawable.fl2
        "ดอกกุหลาบชมพู"       -> com.example.flora.R.drawable.fl3
        "ช่อดอกมิกซ์พาสเทล"  -> com.example.flora.R.drawable.fl4
        "ดอกไม้สีฟ้าพาสเทล"  -> com.example.flora.R.drawable.fl5
        "ดอกออร์คิดขาว"       -> com.example.flora.R.drawable.fl6
        "ดอกกุหลาบชมพูอ่อน"  -> com.example.flora.R.drawable.fl7
        "ดอกไม้ฟ้าคราม"       -> com.example.flora.R.drawable.fl8
        "ดอกเพิร์ชพาสเทล"    -> com.example.flora.R.drawable.fl9
        "ดอกเดซี่มิกซ์"        -> com.example.flora.R.drawable.fl10
        else                   -> com.example.flora.R.drawable.fl
    }
}

@Composable
fun DetailHeader(item: Flowers, waveColor: Color, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(340.dp)) {
        WaveShape(modifier = Modifier.fillMaxWidth().height(320.dp), color = waveColor)

        // ── AsyncImage แทน Image+painterResource ──
        val resId = remember(item.name) { getFlowerResId(item.name) }
        AsyncImage(
            model              = resId,
            contentDescription = item.name,
            modifier           = Modifier.size(300.dp).align(Alignment.Center).offset(y = 30.dp),
            contentScale       = ContentScale.Fit
        )

        IconButton(onClick = onBack, modifier = Modifier.padding(16.dp).align(Alignment.TopStart)) {
            Icon(Icons.Default.ArrowBack, "Back", modifier = Modifier.size(30.dp))
        }
        IconButton(onClick = {}, modifier = Modifier.padding(16.dp).align(Alignment.TopEnd)) {
            Icon(Icons.Default.Share, "Share", modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
fun WaveShape(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, size.height * 0.85f)
            cubicTo(size.width * -0.15f, size.height * 1.05f, size.width * 0.8f, size.height * 0.85f, size.width, size.height * 0.95f)
            lineTo(size.width, 0f)
            close()
        }
        drawPath(path = path, color = color)
    }
}