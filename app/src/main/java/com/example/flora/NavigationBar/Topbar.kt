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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.flora.R



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flora.Authentication.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Topbar(modifier: Modifier = Modifier,navController: NavController,authVM: AuthViewModel){

    val authState by authVM.authState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }


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
                        onClick = {
                            navController.navigate("MainScreen")
                        },
                    ) {
                        AsyncImage(
                            model              = R.drawable.logo,
                            contentDescription = null,
                            modifier           = Modifier.fillMaxSize(),
                            contentScale       = ContentScale.Crop
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

                        // Cart
                        IconButton(
                            onClick = {
                                navController.navigate(("cart"))
                            },
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

                        // Dialog Loggout
                        if (showLogoutDialog) {
                            AlertDialog(
                                onDismissRequest = { showLogoutDialog = false },
                                shape = RoundedCornerShape(24.dp),
                                containerColor = Color(0xFFFAFAFA),
                                tonalElevation = 0.dp,
                                title = {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFF4757).copy(alpha = 0.12f))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Logout,
                                                contentDescription = null,
                                                tint = Color(0xFFFF4757),
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                        Spacer(Modifier.height(12.dp))
                                        Text(
                                            "ออกจากระบบ",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF1A1A1A)
                                        )
                                    }
                                },
                                text = {
                                    Text(
                                        "คุณแน่ใจหรือไม่ว่าต้องการออกจากระบบ?\nคุณจะต้องเข้าสู่ระบบอีกครั้ง",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF888888),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                confirmButton = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                showLogoutDialog = false
                                                authVM.logout()
                                                navController.navigate("login")
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp),
                                            shape = RoundedCornerShape(14.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFFF4757)
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Logout,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                "ออกจากระบบ",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 15.sp
                                            )
                                        }
                                        TextButton(
                                            onClick = { showLogoutDialog = false },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                "ยกเลิก",
                                                color = Color(0xFFAAAAAA),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            )
                        }







                        // Logout
                        IconButton(
                            onClick = {
                                showLogoutDialog = true
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = Color(0x33FFFFFF), // สีพื้นหลัง
                                    shape = RoundedCornerShape(12.dp)
                                )

                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
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