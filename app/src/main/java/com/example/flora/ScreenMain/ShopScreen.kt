package com.example.flora.ScreenMain

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.flora.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun ShopScreen(modifier: Modifier = Modifier,navController: NavController){

    // slide banner
    val banner_slide = listOf(
        R.drawable.banner1,
        R.drawable.rose,
        R.drawable.daisy,
        R.drawable.pinkflower
    )
    val realSize = banner_slide.size // จำนวนรูป banner
    val startPage = Int.MAX_VALUE / 2 // จุดเริ่มต้น
    val pagerState = rememberPagerState(
        initialPage = startPage,
        pageCount = { Int.MAX_VALUE }
    ) // เก็บหน้าปัจจุบัน ควบคุมการ animate

    val coroutineScope = rememberCoroutineScope()
    // End slide banner

    var SearchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                //.padding(16.dp)
                .height(65.dp), // ขนาด Main
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 0.dp)
                    .background(
                        Color(0xFFF1F1F1),
                        shape = RoundedCornerShape(10.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = SearchText,
                    onValueChange = { SearchText = it },
                    placeholder = { Text("Search") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF6400B2),
                            modifier = Modifier
                                .size(30.dp))
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,

                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    )
                )

            }

            Spacer(modifier = Modifier.width(35.dp))

            Row(
                modifier = Modifier
                    .width(60.dp)
                    .fillMaxHeight()
                    .background(
                        Color(0xFF6400B2),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable{

                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.page_info),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                )
            }
        } // Row 1 in Column Main

        Spacer(modifier = Modifier.height(25.dp))


        // Auto Slide
        LaunchedEffect(Unit) {
            while (true){
                delay(3000) // delay 3 s
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) { page ->
                val realPage = page % realSize
                Card(
                    modifier = Modifier
                        //.padding(horizontal = 16.dp)
                        .fillMaxSize()
                        .graphicsLayer{
                            val pageOffset = (
                                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                    ).absoluteValue
                            val scale = 0.85f + (1f - pageOffset.coerceIn(0f,1f)) * 0.15f

                            scaleX = scale
                            scaleY = scale

                        },
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Image(
                        painter = painterResource(banner_slide[realPage]),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop // กัน ขอบขาว
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
            // 🔵 Dot Indicator + Clickable
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                val currentRealPage = pagerState.currentPage % realSize

                repeat(realSize){ index ->
                    val isSelected = currentRealPage == index

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (isSelected) 12.dp else 12.dp) // อันเก่า size(if (isSelected) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected)
                                    Color(0xFF7B2FF7)
                                else
                                    Color.LightGray
                            )
                            .clickable{
                                coroutineScope.launch {
                                    val targetPage = pagerState.currentPage - (currentRealPage - index)
                                    pagerState.animateScrollToPage(targetPage)
                                }
                            }
                    ){

                    } // End Box 1

                }

            } // End Row
        } // End Column Slide

        Spacer(modifier = Modifier.height(20.dp))

        val items = listOf(
            "Box1", "Box2", "Box3",
            "Box4", "Box5", "Box6"
        )
        val colors_flower = listOf(
            "ทั้งหมด" ,"สีฟ้า" , "สีเหลือง",
            "สีม่วง" , "สีแดง"
        )

        var selectedColor by remember { mutableStateOf("ทั้งหมด") }


        // Selected Store
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(items) { item ->

                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(150.dp)
                        .background(
                            Color(0xFF6400B2),
                            shape = RoundedCornerShape(16.dp)
                        )
                        ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "เลือกสีดอกไม้ที่ต้องการ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(colors_flower){ item ->

                val isSelected = item == selectedColor


                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(40.dp)
                        .background(
                            color = if(isSelected)
                                Color(0xFF6400B2)
                            else
                                Color.White,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = if(isSelected)
                                Color(0xFF6400B2)
                            else
                                Color(0xFFD4CCE3),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable{
                            selectedColor = item
                        },
                    contentAlignment = Alignment.Center,
                ){
                    Text(item,
                        color = if(isSelected)
                            Color.White
                        else
                            Color.Black

                    )

                }

            }
        } // End selected Colors






    } // Column Main
}

