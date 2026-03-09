package com.example.flora.ScreenMain

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flora.Firebase.FlowersViewModel
import com.example.flora.Firebase.StoreViewModel
import com.example.flora.R
import com.example.flora.ScreenMain.ShopComponents.FlowerCard
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

// ─────────────────────────────────────────
// แปลง hex → ชื่อสีไทย
// ─────────────────────────────────────────


fun hexToColorName(hex: String): String {
    val clean = hex.trim().uppercase().replace("#", "")
    return when (clean) {
        "EF5350" -> "สีแดง"
        "FF006E" -> "สีชมพู"
        "FFB74D" -> "สีส้ม"
        "FDD835" -> "สีเหลือง"
        "BBDEFB" -> "สีฟ้า"
        "AB47BC" -> "สีม่วง"
        "FAF0E6"  -> "สีขาว"
        "1E88E5" -> "สีน้ำเงิน"


        else -> "สีอื่นๆ"
    }
}

// ── รายการสีทั้งหมดที่มีในระบบ (แสดงเสมอ ไม่ขึ้นกับ DB) ──
val ALL_COLOR_OPTIONS = listOf(
    "ทั้งหมด",
    "สีแดง",
    "สีชมพู",
    "สีส้ม",
    "สีเหลือง",
    "สีเขียว",
    "สีฟ้า",
    "สีม่วง",
    "สีขาว",
    "น้ำเงิน"
)

@Composable
fun ShopScreen(
    modifier        : Modifier = Modifier,
    navController   : NavController,
    flowerViewModel : FlowersViewModel,
    storeViewModel  : StoreViewModel,
) {
    val banner_slide = listOf(
        R.drawable.banner1,
        R.drawable.rose,
        R.drawable.daisy,
        R.drawable.pinkflower
    )
    val realSize   = banner_slide.size
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount   = { Int.MAX_VALUE }
    )

    var searchText    by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("ทั้งหมด") }
    var selectedStore by remember { mutableStateOf<String?>(null) }

    val flowersList by flowerViewModel.allFlowers.collectAsState()

    // ── รายการร้านค้า distinct จาก flower.store ──
    val storeList = remember(flowersList) {
        flowersList.map { it.store }.distinctBy { it.storeID }
    }

    // ── Filter ──
    val filteredList = flowersList.filter { flower ->
        val storeMatch = selectedStore == null || flower.store.storeID == selectedStore
        val colorMatch = selectedColor == "ทั้งหมด" || hexToColorName(flower.categoryColor) == selectedColor
        val nameMatch  = flower.name.contains(searchText, ignoreCase = true)
        storeMatch && colorMatch && nameMatch
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }

    LaunchedEffect(Unit) {
        flowerViewModel.fetchAllFlowers()
        storeViewModel.fetchAllStores()
    }

    LazyColumn(
        modifier       = Modifier.fillMaxSize().background(Color(0xFFF9F7FF)),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // ── 1. Search Bar ──
        item {
            Spacer(modifier = Modifier.height(25.dp))
            Row(
                modifier              = Modifier.fillMaxWidth().height(65.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier          = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Transparent, shape = RoundedCornerShape(10.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value         = searchText,
                        onValueChange = { searchText = it },
                        placeholder   = { Text("ค้นหาดอกไม้ที่ต้องการ") },
                        shape         = RoundedCornerShape(12.dp),
                        modifier      = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF9F7FF))
                            .border(1.dp, Color(0x336400B2), RoundedCornerShape(12.dp)),
                        leadingIcon   = {
                            Icon(Icons.Default.Search, null, tint = Color(0xFF6400B2), modifier = Modifier.size(30.dp))
                        },
                        singleLine    = true,
                        colors        = TextFieldDefaults.colors(
                            focusedContainerColor   = Color(0xFFF9F7FF),
                            unfocusedContainerColor = Color(0xFFF9F7FF),
                            focusedIndicatorColor   = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor  = Color.Transparent,
                            errorIndicatorColor     = Color.Transparent
                        )
                    )
                }
                Spacer(modifier = Modifier.width(15.dp))
                Box(
                    modifier         = Modifier
                        .size(60.dp, 65.dp)
                        .background(Color(0xFF6400B2), shape = RoundedCornerShape(10.dp))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painterResource(id = R.drawable.page_info), null, tint = Color.White, modifier = Modifier.size(30.dp))
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
        }

        // ── 2. Banner ──
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalPager(
                    state    = pagerState,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                ) { page ->
                    val realPage = page % realSize
                    Card(
                        modifier = Modifier.fillMaxSize().graphicsLayer {
                            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                            val scale = 0.85f + (1f - pageOffset.coerceIn(0f, 1f)) * 0.15f
                            scaleX = scale
                            scaleY = scale
                        },
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Image(
                            painter      = painterResource(banner_slide[realPage]),
                            contentDescription = null,
                            modifier     = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                Row(horizontalArrangement = Arrangement.Center) {
                    val currentRealPage = pagerState.currentPage % realSize
                    repeat(realSize) { index ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp).size(12.dp).clip(CircleShape)
                                .background(if (currentRealPage == index) Color(0xFF7B2FF7) else Color.LightGray)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ── 3. Store List ──
        item {
            Text("ร้านค้า", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))
            LazyRow(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(storeList) { store ->
                    val isSelected = selectedStore == store.storeID
                    Column(
                        modifier = Modifier
                            .height(117.dp)
                            .background(
                                if (isSelected) Color(0xFFEDE7F6) else Color(0xFFF3F1F6),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) Color(0xFF6400B2) else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selectedStore = if (selectedStore == store.storeID) null else store.storeID
                            }
                            .padding(15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier         = Modifier.size(62.dp).clip(CircleShape).background(Color(0xFFD1C4E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text       = store.storeName.take(1),
                                fontSize   = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Color(0xFF6400B2)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(store.storeName, color = Color(0xFF6E6A7C), fontWeight = FontWeight.Medium, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ── 4. Color Filter (แสดงทุกสีเสมอ) ──
        item {
            Text("เลือกสีดอกไม้ที่ต้องการ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))
            LazyRow(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ALL_COLOR_OPTIONS) { colorName ->
                    val isSelected = colorName == selectedColor
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) Color(0xFF6400B2) else Color.Transparent,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(2.dp, if (isSelected) Color(0xFF6400B2) else Color(0xFFD4CCE3), RoundedCornerShape(20.dp))
                            .clickable { selectedColor = colorName }
                            .padding(horizontal = 16.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(colorName, fontSize = 16.sp, color = if (isSelected) Color.White else Color.Black)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ── 5. Grid ดอกไม้ (ว่างเปล่าถ้าไม่มีสีนั้น) ──
        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier         = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "ไม่มีดอกไม้${if (selectedColor != "ทั้งหมด") "$selectedColor" else ""}ในขณะนี้",
                        color    = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            val chunkedFlowers = filteredList.chunked(2)
            items(chunkedFlowers) { rowItems ->
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (flower in rowItems) {
                        Box(modifier = Modifier.weight(1f)) {
                            FlowerCard(
                                flower  = flower,
                                onClick = {
                                    flowerViewModel.selectFlower(flower)
                                    navController.navigate("flower_detail")
                                }
                            )
                        }
                    }
                    if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}