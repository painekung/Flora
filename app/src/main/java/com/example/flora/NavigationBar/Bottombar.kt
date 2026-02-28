package com.example.flora.NavigationBar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun Bottombar(
    navController: NavController,
    modifier: Modifier = Modifier,
    shadowHeight: Dp = 20.dp,        // ความสูงของเงา
    shadowAlpha: Float = 0.05f,      // ความเข้มเงา
    shadowColor: Color = Color.Black // สีเงา


){
    // เก็บหน้า ปัจจุบัน
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    //var selectedItem by remember { mutableStateOf(0) } // ตัวแปร เก็บค่า การเปลี่ยน หน้า Bottombar
    //selectedItem = index



    val icons_title_bar = listOf(
        Icons.Default.Home,
        Icons.Default.Shop,
        Icons.Default.Favorite,
        Icons.Default.Person
    )
    val title_bar = listOf("Home","Shop","WhisList","Profile")

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .drawBehind {
                val shadowHeightPx = shadowHeight.toPx()
                // วาดเงาฟุ้งขึ้นด้านบน
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            shadowColor.copy(alpha = shadowAlpha),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = -shadowHeightPx
                    ),
                    topLeft = Offset(0f, -shadowHeightPx),
                    size = size.copy(height = shadowHeightPx)
                )
            } // ทำ เงา ฟุ้ง
            .clip(
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
            ),
        containerColor = Color.White,
        tonalElevation = 0.dp // ปิด elevation ของ NavigationBar
    ) {
        icons_title_bar.forEachIndexed { index,icon ->

            val route = when(index){
                0 -> "MainScreen"
                1 -> "ShopScreen"
                2 -> "WhisListScreen"
                3 -> "ProfileScreen"
                else -> "ProfileScreen"
            }

            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route){ // วิธีนี้ได้ผลดี
                        popUpTo(navController.graph.startDestinationId){
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }

                          },
                icon = {
                    Icon(
                    icon,
                        contentDescription = title_bar[index],
                        modifier = Modifier
                            .size(35.dp)
                    )},
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF6400B2),
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color(0xFF6400B2),
                    unselectedTextColor = Color.Gray
                ),
                label = {
                    Text(title_bar[index],
                    fontSize = 17.sp)
                }
            )
        }
    }







}