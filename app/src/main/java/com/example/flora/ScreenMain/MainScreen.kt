package com.example.flora.ScreenMain.MainScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.flora.Firebase.FlowersViewModel
import com.example.flora.Firebase.StoreViewModel
import com.example.flora.R
import com.example.flora.ScreenMain.ShopComponents.FlowerCard
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

fun PagerState.offsetForPage(page: Int): Float =
    (currentPage - page) + currentPageOffsetFraction

fun lerp(start: Float, stop: Float, fraction: Float): Float =
    start + (stop - start) * fraction.coerceIn(0f, 1f)

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    flowerViewModel: FlowersViewModel,
    storeViewModel: StoreViewModel,
) {
    val bannerSlide = remember {
        listOf(
            R.drawable.banner1, R.drawable.me, R.drawable.hina,
            R.drawable.himeri, R.drawable.momana, R.drawable.erisa, R.drawable.nao
        )
    }
    val categoriesList = remember { listOf("ยอดนิยม", "ตามฤดูกาล", "พรีเมียม", "ราคาประหยัด") }
    val realSize = bannerSlide.size

    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    val context = LocalContext.current

    var selectedColor by remember { mutableStateOf("ยอดนิยม") }
    var searchText    by remember { mutableStateOf("") }

    val flowersList  by flowerViewModel.allFlowers.collectAsState()
    val filteredList  = remember(flowersList, selectedColor, searchText) {
        flowersList.filter {
            it.category == selectedColor && it.name.contains(searchText, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
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
        // 1. Search Bar
        item(key = "search_bar") {
            Spacer(modifier = Modifier.height(25.dp))
            Row(
                modifier              = Modifier.fillMaxWidth().height(65.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier          = Modifier.weight(1f).fillMaxHeight().background(Color.Transparent, shape = RoundedCornerShape(10.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value         = searchText,
                        onValueChange = { searchText = it },
                        placeholder   = { Text("ค้นหาดอกไม้ที่ต้องการ") },
                        shape         = RoundedCornerShape(12.dp),
                        modifier      = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF9F7FF))
                            .border(1.dp, Color(0x336400B2), RoundedCornerShape(12.dp)),
                        leadingIcon   = { Icon(Icons.Default.Search, null, tint = Color(0xFF6400B2), modifier = Modifier.size(30.dp)) },
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
                    modifier = Modifier.size(60.dp, 65.dp)
                        .background(Color(0xFF6400B2), shape = RoundedCornerShape(10.dp))
                        .clickable { Log.d("save", "Flowers List Size: ${flowersList.size}") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(id = R.drawable.page_info), contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
        }

        // 2. Video Player
        item(key = "video_player") {
            VideoPlayer(
                videoUrl  = "android.resource://${context.packageName}/${R.raw.preview}",
                modifier  = Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(16.dp)),
                isVisible = true
            )
            Spacer(modifier = Modifier.height(25.dp))
        }

        // 3. Banner
        item(key = "banner_pager") {
            val currentRealPage by remember { derivedStateOf { pagerState.currentPage % realSize } }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalPager(
                    state                = pagerState,
                    modifier             = Modifier.fillMaxWidth().height(200.dp),
                ) { page ->
                    val realPage = page % realSize

                    val pageOffset by remember(pagerState) { derivedStateOf { pagerState.offsetForPage(page).absoluteValue } }
                    val scale      by remember(pageOffset)  { derivedStateOf { lerp(0.85f, 1f, 1f - pageOffset) } }
                    val alpha      by remember(pageOffset)  { derivedStateOf { lerp(0.6f, 1f, 1f - pageOffset) } }

                    Card(
                        modifier = Modifier.fillMaxSize().graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha },
                        shape    = RoundedCornerShape(15.dp)
                    ) {
                        AsyncImage(
                            model              = bannerSlide[realPage],
                            contentDescription = null,
                            modifier           = Modifier.fillMaxSize(),
                            contentScale       = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))
                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(realSize) { index ->
                        Box(
                            modifier = Modifier.padding(4.dp).size(12.dp).clip(CircleShape)
                                .background(if (currentRealPage == index) Color(0xFF7B2FF7) else Color.LightGray)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
        }

        // 4. Categories
        item(key = "categories") {
            Text("แนะนำสำหรับคุณ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))
            LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categoriesList, key = { it }) { item ->
                    val isSelected = item == selectedColor
                    Box(
                        modifier = Modifier
                            .background(if (isSelected) Color(0xFF6400B2) else Color.Transparent, shape = RoundedCornerShape(20.dp))
                            .border(2.dp, if (isSelected) Color(0xFF6400B2) else Color(0xFFD4CCE3), shape = RoundedCornerShape(20.dp))
                            .clickable { selectedColor = item }
                            .padding(horizontal = 16.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(fontSize = 16.sp, text = item, color = if (isSelected) Color.White else Color.Black)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 5. Flower Grid
        val chunkedFlowers = filteredList.chunked(2)
        items(items = chunkedFlowers, key = { row -> row.firstOrNull()?.flowerID ?: row.hashCode() }) { rowItems ->
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (flower in rowItems) {
                    Box(modifier = Modifier.weight(1f)) {
                        FlowerCard(
                            flower  = flower,
                            onClick = { flowerViewModel.selectFlower(flower); navController.navigate("flower_detail") }
                        )
                    }
                }
                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }

        item(key = "bottom_spacer") { Spacer(modifier = Modifier.height(80.dp)) }
    }
}