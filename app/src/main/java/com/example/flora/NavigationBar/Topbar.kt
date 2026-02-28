package com.example.flora.NavigationBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Topbar(modifier: Modifier = Modifier){
    TopAppBar(
        modifier = modifier
            .height(110.dp), // ✅ เพิ่มความสูงตรงนี้,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF6400B2),
            titleContentColor = Color(0xFFFEFFD3)
        ),
        title = {
            Row( //  Row Main
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically // ✅ เพิ่มตรงนี้
            ) {
                Row( // Row Left
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically // ✅ เพิ่มตรงนี้
                ) {
                    IconButton(
                        onClick = {},
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }


                } // End Row Left

                Row( // Row Right
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically // ✅ เพิ่มตรงนี้
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp), // 👈 ระยะห่าง
                    ) {
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = Color(0x33FFFFFF), // สีพื้นหลัง
                                    shape = RoundedCornerShape(12.dp)
                                )

                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(30.dp)
                            )
                        }

                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = Color(0x33FFFFFF), // สีพื้นหลัง
                                    shape = RoundedCornerShape(12.dp)
                                )

                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(30.dp)
                            )
                        }
                    }

                }// End Row Right

            }// End Row Main
                }, // End title
        ) // End Topbar
}