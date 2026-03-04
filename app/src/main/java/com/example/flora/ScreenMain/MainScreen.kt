package com.example.flora.ScreenMain.MainScreen

import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flora.Firebase.StoreViewModel
import com.example.flora.R
import com.example.flora.ScreenMain.ShopComponents.FlowerCard
import com.example.flora.models.Flower
import com.example.flora.viewmodels.FlowerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    flowerViewModel: FlowerViewModel,
    storeViewModel: StoreViewModel
) {
    // --- Data and States ---
    val banner_slide = listOf(R.drawable.banner1,R.drawable.me,R.drawable.hina, R.drawable.himeri, R.drawable.momana, R.drawable.erisa,R.drawable.nao)
    val colors_flower = listOf("ยอดนิยม", "โรแมนติก", "อบอุ่น", "คิดถึง", "คู่เดต")

    val realSize = banner_slide.size
    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2, pageCount = { Int.MAX_VALUE })
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedColor by remember { mutableStateOf("ยอดนิยม") }
    var searchText by remember { mutableStateOf("") }

    val flowerList = listOf(
        Flower(id = 1, name = "ดอกทิวลิป", rating = 4.9, price = 30.00, image = R.drawable.mute, color = "0xFF7B2FF7", category = "อบอุ่น"),
        Flower(id = 2, name = "ดอกกุหลาบ", rating = 4.8, price = 25.00, image = null, color = "0xFFE91E63", category = "โรแมนติก"),
        Flower(id = 3, name = "ดอกลาเวนเดอร์", rating = 4.7, price = 35.00, image = R.drawable.mute, color = "0xFF9C27B0", category = "ยอดนิยม"),
        Flower(id = 4, name = "ดอกทานตะวัน", rating = 4.9, price = 40.00, image = null, color = "0xFFFFC107", category = "อบอุ่น"),
        Flower(id = 5, name = "ดอกเดซี่", rating = 4.6, price = 20.00, image = R.drawable.mute, color = "0xFFFFFFFF", category = "คู่เดต"),
        Flower(id = 6, name = "ดอกออร์คิด", rating = 4.9, price = 50.00, image = null, color = "0xFFFF4081", category = "คิดถึง"),
    )

    val filteredList = flowerList.filter { it.category == selectedColor }

    // Auto-scroll logic for Banner
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }



    LaunchedEffect("wipha@email.com") {
        storeViewModel.fetchStoreByEmail("wipha@email.com")
        storeViewModel.fetchAllStores()
    }

    // สังเกตการณ์ข้อมูลร้านค้า
    val storeList by storeViewModel.currentStore.collectAsState()

    // --- UI Layout using LazyColumn ---
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {

        // 1. Search Bar Section
        item {
            Spacer(modifier = Modifier.height(25.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(65.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFFF1F1F1), shape = RoundedCornerShape(10.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("ค้นหาดอกไม้ที่ต้องการ") },
                        modifier = Modifier.fillMaxWidth().padding(5.dp),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF6400B2), modifier = Modifier.size(30.dp)) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
                Spacer(modifier = Modifier.width(15.dp))
                Box(
                    modifier = Modifier
                        .size(60.dp, 65.dp)
                        .background(Color(0xFF6400B2), shape = RoundedCornerShape(10.dp))
                        .clickable {
                            Log.d("save",storeList.toString())
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(id = R.drawable.page_info), contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
        }

        // 2. Video Player Section
        item {
            VideoPlayer(
                videoUrl = "android.resource://${context.packageName}/${R.raw.preview}",
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(16.dp)),
                isVisible = true
            )
            Spacer(modifier = Modifier.height(25.dp))
        }

        // 3. Banner Section
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalPager(
                    state = pagerState,
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
                            painter = painterResource(banner_slide[realPage]),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
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
            Spacer(modifier = Modifier.height(25.dp))
        }

        // 4. Recommendation Header & Category List
        item {
            Text(text = "แนะนำสำหรับคุณ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(colors_flower) { item ->
                    val isSelected = item == selectedColor
                    Box(
                        modifier = Modifier
                            .background(if (isSelected) Color(0xFF6400B2) else Color.White, shape = RoundedCornerShape(20.dp))
                            .border(2.dp, if (isSelected) Color(0xFF6400B2) else Color(0xFFD4CCE3), shape = RoundedCornerShape(20.dp))
                            .clickable { selectedColor = item }
                            .padding(horizontal = 16.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item, color = if (isSelected) Color.White else Color.Black)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 5. Grid of Flowers (Using chunked to simulate 2 columns)
        val chunkedFlowers = filteredList.chunked(2)
        items(chunkedFlowers) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (flower in rowItems) {
                    Box(modifier = Modifier.weight(1f)) {
                        FlowerCard(
                            flower = flower,
                            onClick = {
                                flowerViewModel.selectedFlower = flower
                                navController.navigate("flower_detail")
                            }
                        )
                    }
                }
                // If only 1 item in the last row, add an empty spacer for alignment
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // 6. Bottom Spacer
        item {
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}