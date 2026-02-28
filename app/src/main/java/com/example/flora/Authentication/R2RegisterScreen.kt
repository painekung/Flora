package com.example.flora.Authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MarkunreadMailbox
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun RegisterScreenTwo(modifier: Modifier = Modifier,navController: NavController){
    var tel by remember { mutableStateOf("") }

    var addressLine by remember { mutableStateOf("") }
    var subDistrict by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xFF6400B2).copy(alpha = 0.15f),
                        0.15f to Color(0xFFEBD9FF),
                        0.6f to Color.White,
                        1.0f to Color.White
                    )
                )
            )

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
        ) {
            Row( // Row Back Home
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    shape = RoundedCornerShape(10.dp),   // ความโค้ง
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6400B2), // สีพื้นหลัง
                        contentColor = Color.White  // สีตัวอักษร
                    ),
                    modifier = Modifier.size(52.dp),     // ขนาดสี่เหลี่ยม
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            } // End Row Back Home


            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sign Up",
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Address",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))


                // บ้านเลขที่ / ถนน
                OutlinedTextField(
                    value = addressLine,
                    onValueChange = { addressLine = it },
                    placeholder = { Text("House No. / Road") },
                    textStyle = TextStyle(fontSize = 23.sp),
                    leadingIcon = {
                        Icon(Icons.Default.Home, contentDescription = null)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    maxLines = 2
                )

                // ตำบล / แขวง
                OutlinedTextField(
                    value = subDistrict,
                    onValueChange = { subDistrict = it },
                    placeholder = { Text("Sub-district") },
                    textStyle = TextStyle(fontSize = 23.sp),
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                )

                // อำเภอ / เขต
                OutlinedTextField(
                    value = district,
                    onValueChange = { district = it },
                    placeholder = { Text("District") },
                    textStyle = TextStyle(fontSize = 23.sp),
                    leadingIcon = {
                        Icon(Icons.Default.LocationCity, contentDescription = null)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                )

                // จังหวัด
                OutlinedTextField(
                    value = province,
                    onValueChange = { province = it },
                    placeholder = { Text("Province") },
                    textStyle = TextStyle(fontSize = 23.sp),
                    leadingIcon = {
                        Icon(Icons.Default.Public, contentDescription = null)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                )


                // รหัสไปรษณีย์
                OutlinedTextField(
                    value = postalCode,
                    onValueChange = { postalCode = it.filter { ch -> ch.isDigit() } },
                    placeholder = { Text("Postal Code") },
                    textStyle = TextStyle(fontSize = 23.sp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.MarkunreadMailbox, contentDescription = null)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tel.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // เบอร์โทร
                OutlinedTextField(
                    value = tel,
                    onValueChange = { tel = it.filter { ch -> ch.isDigit() } },
                    placeholder = { Text("Tel.") },
                    textStyle = TextStyle(fontSize = 23.sp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                )


                // --------------- Button Register ----------------- //
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF6400B2),   // ม่วงหลัก
                                    Color(0xFF9C27B0)    // ม่วงชมพูอ่อน
                                )
                            )
                        )
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = {
                            navController.navigate("home"){
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            text = "Register",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                    }
                }





            }
        }

    } // End Column Main
}
